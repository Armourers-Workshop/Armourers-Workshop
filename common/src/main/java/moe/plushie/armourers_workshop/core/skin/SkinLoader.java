package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinFileProvider;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.network.RequestSkinPacket;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinServerType;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.SkinCipher;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.WorkQueue;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SkinLoader {

    private static final SkinLoader LOADER = new SkinLoader();

    private final EnumMap<DataDomain, Session> taskManager = new EnumMap<>(DataDomain.class);

    private final WorkQueue workQueue = new WorkQueue();
    private final HashMap<String, IResultHandler<Skin>> waiting = new HashMap<>();
    private final HashMap<String, ISkinFileProvider> loaders = new HashMap<>();
    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, GlobalEntry> globalEntries = new ConcurrentHashMap<>();

    private SkinLoader() {
        this.setup(SkinServerType.CLIENT);
    }

    public static SkinLoader getInstance() {
        return LOADER;
    }

    public void setup(SkinServerType type) {
        taskManager.values().forEach(Session::shutdown);
        var local = new LocalDataSession();
        var download = new DownloadSession();
        var slice = new SliceSession();
        if (type == SkinServerType.CLIENT) {
            var proxy = new ProxySession();
            taskManager.put(DataDomain.LOCAL, local);
            taskManager.put(DataDomain.DATABASE, proxy);
            taskManager.put(DataDomain.DATABASE_LINK, proxy);
            taskManager.put(DataDomain.DEDICATED_SERVER, proxy);
            taskManager.put(DataDomain.GLOBAL_SERVER, proxy);
            taskManager.put(DataDomain.GLOBAL_SERVER_PREVIEW, download);
            taskManager.put(DataDomain.SLICE_LOAD, slice);
        } else {
            taskManager.put(DataDomain.LOCAL, local);
            taskManager.put(DataDomain.DATABASE, local);
            taskManager.put(DataDomain.DATABASE_LINK, local);
            taskManager.put(DataDomain.DEDICATED_SERVER, local);
            taskManager.put(DataDomain.GLOBAL_SERVER, download);
            taskManager.put(DataDomain.GLOBAL_SERVER_PREVIEW, download);
            taskManager.put(DataDomain.SLICE_LOAD, slice);
        }
    }

    public void register(DataDomain domain, ISkinFileProvider loader) {
        this.loaders.put(domain.namespace(), loader);
    }

    @Nullable
    public Skin getSkin(ItemStack itemStack) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return null;
        }
        return getSkin(descriptor.getIdentifier());
    }

    @Nullable
    public Skin getSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        var entry = getEntry(identifier);
        if (entry != null) {
            return entry.get();
        }
        return null;
    }

    @Nullable
    public Skin loadSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        var entry = getOrCreateEntry(identifier);
        resumeRequest(entry, Method.SYNC);
        return entry.get();
    }

    public void loadSkin(String identifier, @Nullable IResultHandler<Skin> handler) {
        var entry = getOrCreateEntry(identifier);
        entry.listen(handler);
        resumeRequest(entry, Method.ASYNC);
    }

    public SkinDescriptor loadSkinFromDB(String identifier, ColorScheme scheme, boolean needCopy) {
        var skin = loadSkin(identifier);
        if (skin != null) {
            if (needCopy) {
                identifier = saveSkin(identifier, skin);
            }
            return new SkinDescriptor(identifier, skin.getType(), scheme);
        }
        return SkinDescriptor.EMPTY;
    }

    public void loadSkinFromDB(String identifier, ColorScheme scheme, IResultHandler<SkinDescriptor> handler) {
        // merge all request into one.
        getOrCreateGlobalEntry(identifier).resume((descriptor, exception) -> {
            if (descriptor != null) {
                descriptor = new SkinDescriptor(descriptor, scheme);
            }
            handler.apply(descriptor, exception);
        });
    }

//    public void loadSkinFromDB(String identifier, ColorScheme scheme, boolean needCopy, IResultHandler<SkinDescriptor> handler) {
//        backend.execute(() -> {
//            try {
//                ModLog.debug("'{}' => preload into database", identifier);
//                handler.accept(loadSkinFromDB(identifier, scheme, needCopy));
////                caches.add(handler);
//            } catch (Exception exception) {
//                handler.reject(exception);
//            }
//        });
//    }

    public String saveSkin(String identifier, Skin skin) {
        if (DataDomain.isDatabase(identifier)) {
            return identifier;
        }
        var newIdentifier = LocalDataService.getInstance().saveSkinFile(skin);
        if (newIdentifier != null) {
            identifier = DataDomain.DATABASE.normalize(newIdentifier);
            addSkin(identifier, skin);
        }
        return identifier;
    }


    public void addSkin(String identifier, Skin skin) {
        var entry = getOrCreateEntry(identifier);
        entry.accept(skin);
    }

    public void addSkin(String identifier, Skin skin, Exception exception) {
        ModLog.debug("'{}' => receive server skin, exception: {}", identifier, exception);
        var resultHandler = waiting.remove(identifier);
        if (resultHandler != null) {
            resultHandler.apply(skin, exception);
        }
    }

    public void removeSkin(String identifier) {
        var entry = removeEntry(identifier);
        if (entry != null && !entry.isCompleted()) {
            entry.abort(new CancellationException("removed by user"));
        }
    }

    public synchronized void prepare(SkinServerType type) {
        ModLog.debug("prepare skin loader");
        setup(type);
    }

    public synchronized void start() {
        ModLog.debug("start skin loader");
        workQueue.resume();
    }

    public synchronized void stop() {
        ModLog.debug("stop skin loader");
        workQueue.pause();
        waiting.clear();
        entries.clear();
        globalEntries.clear();
        setup(SkinServerType.CLIENT);
    }

    public void submit(Runnable cmd) {
        workQueue.submit(cmd);
    }

    private Entry getEntry(String identifier) {
        return entries.get(identifier);
    }

    private Entry getOrCreateEntry(String identifier) {
        return entries.computeIfAbsent(identifier, Entry::new);
    }

    private GlobalEntry getOrCreateGlobalEntry(String identifier) {
        return globalEntries.computeIfAbsent(identifier, GlobalEntry::new);
    }

    private Entry removeEntry(String identifier) {
        return entries.remove(identifier);
    }

    private void resumeRequest(Entry entry, Method method) {
        // the task although loading is completed,
        // but it is released for memory reasons,
        if (entry.isCompleted()) {
            if (!entry.isReleased()) {
                return;
            }
        }
        var session = taskManager.get(DataDomain.byName(entry.identifier));
        if (session == null) {
            entry.abort(new NoSuchElementException("can't found session"));
            return;
        }
        var req = session.request(method, entry.identifier);
        req.delegate = entry;
        session.submit(req);
    }

    public enum Status {
        PENDING, LOADING, FINISHED, CANCELLED, ABORTED;

        public boolean isCompleted() {
            return this == FINISHED || this == ABORTED;
        }
    }

    public enum Method {
        ASYNC, SOFT_SYNC, SYNC
    }

    public static class Entry {

        public final String identifier;

        public SoftReference<Skin> skin;
        public Exception exception;
        public Status status = Status.PENDING;

        public ArrayList<IResultHandler<Skin>> handlers = new ArrayList<>();

        public Entry(String identifier) {
            this.identifier = identifier;
        }

        public void accept(Skin skin) {
            ModLog.debug("'{}' => finish skin loading", identifier);
            this.skin = new SoftReference<>(skin);
            this.exception = null;
            this.status = Status.FINISHED;
            this.invoke();
        }

        public void abort(Exception exception) {
            ModLog.debug("'{}' => abort skin loading, exception: {}", identifier, exception);
            this.skin = null;
            this.exception = exception;
            this.status = Status.ABORTED;
            // when load is time out, we'll load it again the next time
            if (exception instanceof TimeoutException || exception instanceof CancellationException) {
                this.status = Status.PENDING;
            }
            this.invoke();
        }

        public void invoke() {
            if (this.handlers.isEmpty()) {
                return;
            }
            var handlers = this.handlers;
            this.handlers = new ArrayList<>();
            handlers.forEach(handler -> handler.apply(get(), exception));
        }

        public void listen(@Nullable IResultHandler<Skin> handler) {
            if (isCompleted()) {
                if (handler != null) {
                    handler.apply(get(), exception);
                }
                return;
            }
            if (handler != null) {
                handlers.add(handler);
            }
        }

        public boolean isCompleted() {
            return status.isCompleted();
        }

        public boolean isReleased() {
            if (skin != null) {
                return skin.get() == null;
            }
            return false;
        }

        public Skin get() {
            if (skin != null) {
                return skin.get();
            }
            return null;
        }
    }

    public static class GlobalEntry implements IResultHandler<Skin> {

        private boolean isFinished = false;
        private boolean isRequested = false;

        private SkinDescriptor descriptor = SkinDescriptor.EMPTY;
        private Exception exception;

        private final String identifier;
        private final ArrayList<IResultHandler<SkinDescriptor>> pending = new ArrayList<>();

        public GlobalEntry(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void apply(Skin skin, Exception exception) {
            this.isFinished = true;
            this.exception = exception;
            if (skin == null) {
                sendNotify();
                return;
            }
            var newIdentifier = LOADER.saveSkin(identifier, skin);
            ModLog.debug("'{}' => did load global skin into database, target: '{}'", newIdentifier);
            descriptor = new SkinDescriptor(newIdentifier, skin.getType(), ColorScheme.EMPTY);
            sendNotify();
        }

        public void resume(IResultHandler<SkinDescriptor> handler) {
            if (isFinished) {
                handler.apply(descriptor, exception);
                return;
            }
            pending.add(handler);
            if (!isRequested) {
                isRequested = true;
                ModLog.debug("'{}' => load global skin into database", identifier);
                LOADER.loadSkin(identifier, this);
            }
        }

        private void sendNotify() {
            for (var handler : pending) {
                handler.apply(descriptor, exception);
            }
            pending.clear();
        }
    }

    public static class Request {

        private final String identifier;
        private int level = 0;
        private boolean isRunning = false;
        private Method method = Method.ASYNC;
        private Entry delegate;

        public Request(String identifier) {
            this.identifier = identifier;
        }

        public void accept(Skin skin) {
            if (this.delegate != null) {
                this.delegate.accept(skin);
                this.delegate = null;
            }
        }

        public void abort(Exception exception) {
            if (this.delegate != null) {
                this.delegate.abort(exception);
                this.delegate = null;
            }
        }

        public void elevate(Method method) {
            this.level += 1;
            if (this.method.ordinal() < method.ordinal()) {
                this.method = method;
            }
        }
    }

    public static abstract class Session {

        protected final HashMap<String, Request> requests = new HashMap<>();

        protected final ExecutorService executor;

        private boolean isRunning = false;

        public Session(String name) {
            this.executor = buildThreadPool(name, 1);
        }

        public abstract Skin load(Request request) throws Exception;

        public Request request(Method method, String identifier) {
            var task = getRequest(identifier);
            task.elevate(method);
            return task;
        }

        public void submit(Request request) {
            if (request.method != Method.ASYNC) {
                run(request);
                return;
            }
            if (!isRunning) {
                isRunning = true;
                executor.execute(this::run);
            }
        }

        public void shutdown() {
            requests.clear();
            executor.shutdownNow();
        }

        protected void run() {
            while (true) {
                Request task = pollRequest();
                if (task != null) {
                    run(task);
                } else {
                    break;
                }
            }
            synchronized (this) {
                isRunning = false;
            }
        }

        protected void run(Request request) {
            // this request is executed in another thread, so we must wait it complete.
            if (request.isRunning) {
                syncRequest(request);
                return;
            }
            ModLog.debug("'{}' => start load skin", request.identifier);
            request.isRunning = true;
            try {
                var skin = load(request);
                request.accept(skin);
            } catch (Exception exception) {
                exception.printStackTrace();
                request.abort(exception);
            }
            request.isRunning = false;
        }

        protected ExecutorService buildThreadPool(String name, int size) {
            return ThreadUtils.newFixedThreadPool(size, name, Thread.MIN_PRIORITY);
        }

        private void syncRequest(Request request) {
            try {
                ModLog.debug("'{}' => await load skin", request.identifier);
                var semaphore = new Semaphore(0);
                request.delegate.listen((skin, exception) -> semaphore.release());
                semaphore.acquire();
                ModLog.debug("'{}' => await load skin completed", request.identifier);
            } catch (Exception exception) {
                exception.printStackTrace();
                ModLog.debug("'{}' => await load skin failed", request.identifier);
            }
        }

        private synchronized Request pollRequest() {
            if (requests.isEmpty()) {
                return null;
            }
            var request = Collections.max(requests.values(), Comparator.comparingInt(t -> t.level));
            if (request != null) {
                return requests.remove(request.identifier);
            }
            return null;
        }

        private synchronized Request getRequest(String identifier) {
            return requests.computeIfAbsent(identifier, Request::new);
        }
    }

    public static abstract class LoadingSession extends Session {

        public LoadingSession(String name) {
            super(name);
        }

        @Override
        public Skin load(Request request) throws Exception {
            InputStream inputStream = null;
            try {
                inputStream = from(request);
                var startTime = System.currentTimeMillis();
                var skin = SkinFileStreamUtils.loadSkinFromStream2(inputStream);
                var totalTime = System.currentTimeMillis() - startTime;
                loadDidFinish(request, skin, totalTime);
                return skin;
            } finally {
                StreamUtils.closeQuietly(inputStream);
            }
        }

        public abstract void loadDidFinish(Request request, Skin skin, long loadTime);

        public abstract InputStream from(Request request) throws Exception;
    }

    public static class LocalDataSession extends LoadingSession {

        public LocalDataSession() {
            super("AW-SKIN-LD");
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from local session, time: {}ms", request.identifier, loadTime);
        }

        @Override
        public InputStream from(Request request) throws Exception {
            return DataManager.getInstance().loadSkinData(request.identifier);
        }
    }

    public static class ProxySession extends Session {

        private final Semaphore available = new Semaphore(0, true);

        private final CacheSession caching = new CacheSession();

        public ProxySession() {
            super("AW-SKIN-PR");
        }

        @Override
        public void shutdown() {
            super.shutdown();
            this.caching.shutdown();
        }

        @Override
        public Skin load(Request request) throws Exception {
            try {
                return caching.load(request);
            } catch (Exception ignored) {
            }
            ModLog.debug("'{}' => start request server skin", request.identifier);
            var req = new RequestSkinPacket(request.identifier);
            NetworkManager.sendToServer(req);
            return await(request);
        }

        public Skin await(Request request) throws Exception {
            LockState state = new LockState(available);
            LOADER.waiting.put(request.identifier, (skin, exception) -> receive(request, state, skin, exception));
            ModLog.debug("'{}' => await server response", request.identifier);
            boolean ignored = available.tryAcquire(30, TimeUnit.SECONDS);
            state.timeout();
            if (state.skin != null) {
                caching.add(request.identifier, state.skin);
                return state.skin;
            }
            if (state.exception == null) {
                state.exception = new TimeoutException("request server skin");
            }
            throw state.exception;
        }

        public void receive(Request request, LockState state, Skin skin, Exception exception) {
            state.skin = skin;
            state.exception = exception;
            // still waiting to response
            if (state.available != null) {
                state.release();
                return;
            }
            // we are late, but we can still save the data to the cache.
            if (skin != null) {
                caching.add(request.identifier, skin);
            }
        }

        static class LockState {
            Semaphore available;
            Skin skin;
            Exception exception;

            LockState(Semaphore semaphore) {
                this.available = semaphore;
            }

            synchronized void timeout() {
                this.available = null;
            }

            synchronized void release() {
                Semaphore available = this.available;
                this.available = null;
                if (available != null) {
                    available.release();
                }
            }
        }
    }

    public static class DownloadSession extends LoadingSession {

        private final CacheSession caching = new CacheSession();

        public DownloadSession() {
            super("AW-SKIN-DL");
        }

        @Override
        public void shutdown() {
            super.shutdown();
            this.caching.shutdown();
        }

        @Override
        public Skin load(Request request) throws Exception {
            var cachedFile = caching.cachingFile(request.identifier);
            if (cachedFile.exists()) {
                try {
                    return caching.load(request);
                } catch (Exception ignored) {
                    // yes, it's failure, we need to roll back to the download from network.
                }
            }
            return super.load(request);
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from download session, time: {}ms", request.identifier, loadTime);
            caching.add(request.identifier, skin);
        }

        @Override
        public InputStream from(Request request) throws Exception {
            var domain = DataDomain.getNamespace(request.identifier);
            var loader = LOADER.loaders.get(domain);
            if (loader != null) {
                return loader.loadSkin(DataDomain.getPath(request.identifier));
            }
            throw new RuntimeException("can't support the '" + domain + "' protocol");
        }

        @Override
        protected ExecutorService buildThreadPool(String name, int size) {
            return super.buildThreadPool(name, 2);
        }
    }

    public static class CacheSession extends LoadingSession {

        public CacheSession() {
            super("AW-SKIN-CH");
        }

        public void add(String identifier, Skin skin) {
            var cachedFile = cachingFile(identifier);
            if (skin == null || cachedFile == null) {
                return;
            }
            // global data no need decrypt/encrypt
            if (isGlobalLibraryResource(identifier)) {
                ModLog.debug("'{}' => add global skin cache", identifier);
                executor.execute(() -> {
                    FileOutputStream outputStream = null;
                    try {
                        SkinFileUtils.forceMkdirParent(cachedFile);
                        if (cachedFile.exists()) {
                            SkinFileUtils.deleteQuietly(cachedFile);
                        }
                        outputStream = new FileOutputStream(cachedFile);
                        SkinFileStreamUtils.saveSkinToStream(outputStream, skin);
                        outputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    StreamUtils.closeQuietly(outputStream);
                });
                return;
            }
            byte[] x0 = ModContext.x0();
            byte[] x1 = ModContext.x1();
            if (x0 == null || x1 == null) {
                return;
            }
            ModLog.debug("'{}' => add skin cache", identifier);
            executor.execute(() -> {
                FileOutputStream fileOutputStream = null;
                CipherOutputStream cipherOutputStream = null;
                try {
                    SkinFileUtils.forceMkdirParent(cachedFile);
                    if (cachedFile.exists()) {
                        SkinFileUtils.deleteQuietly(cachedFile);
                    }
                    fileOutputStream = new FileOutputStream(cachedFile);
                    if (x1.length != 0) {
                        fileOutputStream.write(x0);
                        SecretKeySpec key = new SecretKeySpec(x1, "AES");
                        Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        aes.init(Cipher.ENCRYPT_MODE, key);
                        cipherOutputStream = new CipherOutputStream(fileOutputStream, aes);
                        SkinFileStreamUtils.saveSkinToStream(cipherOutputStream, skin);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StreamUtils.closeQuietly(cipherOutputStream, fileOutputStream);
            });
        }

        public void remove(String identifier) {
            var cachedFile = cachingFile(identifier);
            if (cachedFile == null || !cachedFile.exists()) {
                return;
            }
            ModLog.debug("'{}' => remove skin cache", identifier);
            executor.execute(() -> {
                SkinFileUtils.deleteQuietly(cachedFile);
            });
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from cache session, time: {}ms", request.identifier, loadTime);
        }

        @Override
        public InputStream from(Request request) throws Exception {
            var cacheFile = cachingFile(request.identifier);
            if (cacheFile == null) {
                throw new FileNotFoundException(request.identifier);
            }
            // global data no need decrypt/encrypt
            if (isGlobalLibraryResource(request.identifier)) {
                return new FileInputStream(cacheFile);
            }
            var x0 = ModContext.x0();
            var x1 = ModContext.x1();
            if (x0 == null || x1 == null) {
                throw new IllegalStateException("illegal context state");
            }
            var inputStream = new FileInputStream(cacheFile);
            var target = new byte[x0.length];
            var targetSize = inputStream.read(target, 0, target.length);
            if (targetSize != x0.length || !Arrays.equals(x0, target)) {
                throw new IllegalStateException("illegal cache signature");
            }
            var key = new SecretKeySpec(x1, "AES");
            var aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, key);
            return new CipherInputStream(inputStream, aes);
        }

        private File cachingFile(String identifier) {
            File cacheFile = null;
            var t0 = ModContext.t0();
            var namespace = DataDomain.getNamespace(identifier);
            if (isGlobalLibraryResource(identifier) && ModConfig.Common.isGlobalSkinServer()) {
                var path = DataDomain.getPath(identifier);
                var domain = "00000000-0000-0000-0000-000000000000";
                // for history reasons and performance optimization,
                // when skin already downloaded we don't need preview skin.
                cacheFile = cachingFile(domain, DataDomain.GLOBAL_SERVER.namespace(), path);
                if (!cacheFile.exists()) {
                    cacheFile = cachingFile(domain, namespace, path);
                }
            } else if (t0 != null) {
                var path = DataDomain.getPath(identifier);
                cacheFile = cachingFile(t0.toString(), namespace, path);
            }
            return cacheFile;
        }

        private File cachingFile(String domain, String namespace, String identifier) {
            var rootPath = new File(EnvironmentManager.getSkinCacheDirectory(), domain);
            var localPath = new File(rootPath, namespace);
            return new File(localPath, identifier + ".dat");
        }

        private boolean isGlobalLibraryResource(String identifier) {
            return DataDomain.GLOBAL_SERVER.matches(identifier) || DataDomain.GLOBAL_SERVER_PREVIEW.matches(identifier);
        }
    }

    public static class SliceSession extends Session {

        public SliceSession() {
            super("AW-SKIN-SL");
        }

        @Override
        public Skin load(Request request) throws Exception {
            var parts = SkinCipher.getInstance().decrypt(DataDomain.getPath(request.identifier));
            if (parts.length != 2) {
                throw new RuntimeException("invalid identifier format!");
            }
            var id = parts[0];
            var keyPath = parts[1];
            var skin = LOADER.loadSkin(id);
            if (skin == null || !skin.getSettings().isEditable()) {
                throw new RuntimeException("can't load skin " + id);
            }
            var skinPart = extractPart(keyPath, skin.getParts());
            if (skinPart == null) {
                throw new RuntimeException("can't load part " + keyPath + " in " + id);
            }
            var animations = new ArrayList<SkinAnimation>();
            skin.getAnimations().forEach(animation -> {
                var keys = animation.getValues().keySet();
                if (containsPart(keys, skinPart)) {
                    animations.add(animation);
                }
            });
            var builder = new Skin.Builder(SkinTypes.ADVANCED);
            builder.paintData(skin.getPaintData());
            builder.version(skin.getVersion());
            builder.parts(Collections.singleton(skinPart));
            builder.settings(skin.getSettings().copy());
            builder.properties(skin.getProperties().copy());
            builder.animations(animations);
            return builder.build();
        }

        SkinPart extractPart(String keyPath, List<SkinPart> parts) {
            SkinPart part = null;
            for (String key : keyPath.split("[.]")) {
                part = findPart(key, parts);
                if (part == null) {
                    return null;
                }
                parts = part.getParts();
            }
            return part;
        }

        SkinPart findPart(String key, List<SkinPart> parts) {
            for (SkinPart part : parts) {
                if (key.equals(part.getName())) {
                    return part;
                }
            }
            int size = parts.size();
            for (int i = 0; i < size; ++i) {
                if (key.equals(String.valueOf(i))) {
                    return parts.get(i);
                }
            }
            return null;
        }

        boolean containsPart(Set<String> names, SkinPart part) {
            var name = part.getName();
            if (name != null && names.contains(name)) {
                return true;
            }
            for (var child : part.getParts()) {
                if (containsPart(names, child)) {
                    return true;
                }
            }
            return false;
        }
    }
}
