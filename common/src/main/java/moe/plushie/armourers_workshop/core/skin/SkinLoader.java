package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.serializer.ISkinFileProvider;
import moe.plushie.armourers_workshop.core.data.DataAlgorithm;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.network.RequestSkinPacket;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenCipher;
import moe.plushie.armourers_workshop.core.utils.OpenDistributionType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
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
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    private final TaskQueue workQueue = new TaskQueue();
    private final HashMap<String, IResultHandler<Skin>> waiting = new HashMap<>();
    private final HashMap<String, ISkinFileProvider> loaders = new HashMap<>();
    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, GlobalEntry> globalEntries = new ConcurrentHashMap<>();

    private SkinLoader() {
        this.setup(OpenDistributionType.CLIENT);
    }

    public static SkinLoader getInstance() {
        return LOADER;
    }

    private void setup(OpenDistributionType type) {
        // we need shutdown all old sessions (only shutdown once).
        if (!taskManager.isEmpty()) {
            var shutdownQueue = new LinkedHashSet<>(taskManager.values());
            shutdownQueue.forEach(Session::shutdown);
            taskManager.clear();
        }
        var local = new LocalDataSession();
        var pack = new ResourcePackSession();
        var download = new DownloadSession();
        var slice = new SliceSession();
        if (type.isClient()) {
            var proxy = new ProxySession();
            taskManager.put(DataDomain.LOCAL, local);
            taskManager.put(DataDomain.RESOURCE_PACK, pack);
            taskManager.put(DataDomain.DATABASE, proxy);
            taskManager.put(DataDomain.DATABASE_LINK, proxy);
            taskManager.put(DataDomain.DEDICATED_SERVER, proxy);
            taskManager.put(DataDomain.GLOBAL_SERVER, proxy);
            taskManager.put(DataDomain.GLOBAL_SERVER_PREVIEW, download);
            taskManager.put(DataDomain.SLICE_LOAD, slice);
        } else {
            taskManager.put(DataDomain.LOCAL, local);
            taskManager.put(DataDomain.RESOURCE_PACK, pack);
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
        return getSkin(descriptor.identifier());
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
        resume(entry, Method.SYNC);
        return entry.get();
    }

    public void loadSkin(String identifier, @Nullable IResultHandler<Skin> handler) {
        var entry = getOrCreateEntry(identifier);
        entry.listen(handler);
        resume(entry, Method.ASYNC);
    }

    public SkinDescriptor loadSkinFromDB(String identifier, SkinPaintScheme scheme, boolean needCopy) {
        var skin = loadSkin(identifier);
        if (skin != null) {
            if (needCopy) {
                identifier = saveSkin(identifier, skin);
            }
            return new SkinDescriptor(identifier, skin.type(), scheme);
        }
        return SkinDescriptor.EMPTY;
    }

    public void loadSkinFromDB(String identifier, SkinPaintScheme scheme, IResultHandler<SkinDescriptor> handler) {
        // merge all request into one.
        getOrCreateGlobalEntry(identifier).resume((descriptor, exception) -> {
            if (descriptor != null) {
                descriptor = descriptor.withPaintScheme(scheme);
            }
            handler.apply(descriptor, exception);
        });
    }

    public InputStream loadSkinData(String identifier) throws Exception {
        var session = taskManager.get(DataDomain.byName(identifier));
        if (session instanceof LoadingSession loadingSession) {
            return loadingSession.loadData(identifier);
        }
        throw new RuntimeException("can't support method in session");
    }

    public void loadSkinInfo(String identifier, @Nullable IResultHandler<Skin> handler) {
        loadSkin(identifier, handler);
    }

    public String saveSkin(String identifier, Skin skin) {
        try {
            // when the skin is already in the database, we not need to modify it.
            if (DataDomain.isDatabase(identifier)) {
                return identifier;
            }
            var newIdentifier = DataManager.getInstance().saveSkin(null, skin);
            identifier = DataDomain.DATABASE.normalize(newIdentifier);
            addSkin(identifier, skin);
            return identifier;
        } catch (Exception exception) {
            ModLog.error("can't save {} into database", identifier, exception);
            return "";
        }
    }


    public void addSkin(String identifier, Skin skin) {
        var entry = getOrCreateEntry(identifier);
        entry.accept(skin);
    }

    public void addSkin(String identifier, Skin skin, Throwable exception) {
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

    public synchronized void prepare(OpenDistributionType type) {
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
        setup(OpenDistributionType.CLIENT);
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

    private void resume(Entry entry, Method method) {
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

    private static class Entry {

        public final String identifier;

        public SoftReference<Skin> skin;
        public Throwable exception;
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

        public void abort(Throwable exception) {
            ModLog.error("'{}' => abort skin loading", identifier, exception);
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

    private static class GlobalEntry implements IResultHandler<Skin> {

        private boolean isFinished = false;
        private boolean isRequested = false;

        private SkinDescriptor descriptor = SkinDescriptor.EMPTY;
        private Throwable exception;

        private final String identifier;
        private final ArrayList<IResultHandler<SkinDescriptor>> pending = new ArrayList<>();

        public GlobalEntry(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void apply(Skin skin, Throwable exception) {
            this.isFinished = true;
            this.exception = exception;
            if (skin == null) {
                sendNotify();
                return;
            }
            var newIdentifier = LOADER.saveSkin(identifier, skin);
            ModLog.debug("'{}' => did load global skin into database, target: '{}'", newIdentifier);
            descriptor = new SkinDescriptor(newIdentifier, skin.type(), SkinPaintScheme.EMPTY);
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

    private static class Request {

        private final String identifier;

        private int level = 0;
        private boolean isRunning = false;

        private SkinFileOptions options;

        private Method method = Method.ASYNC;
        private Entry delegate;

        public Request(String identifier) {
            this.identifier = identifier;
            this.options = null;
        }

        public void accept(Skin skin) {
            if (this.delegate != null) {
                this.delegate.accept(skin);
                this.delegate = null;
            }
        }

        public void abort(Throwable exception) {
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

    private static abstract class Session {

        protected final HashMap<String, Request> requests = new HashMap<>();

        protected final ExecutorService executor;

        private boolean isRunning = false;

        public Session(String name) {
            this.executor = buildThreadPool(name, 1);
        }

        public abstract Skin load(Request request) throws Throwable;

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
                var task = pollRequest();
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
            ModLog.debug("'{}' => start skin loading", request.identifier);
            request.isRunning = true;
            try {
                var skin = load(request);
                request.accept(skin);
            } catch (Throwable exception) {
                request.abort(exception);
            }
            request.isRunning = false;
        }

        protected ExecutorService buildThreadPool(String name, int size) {
            return Executors.newFixedThreadPool(size, name, Thread.MIN_PRIORITY);
        }

        private void syncRequest(Request request) {
            try {
                // when the delegate is released, we consider the request is completed.
                var delegate = request.delegate;
                if (delegate == null) {
                    return;
                }
                ModLog.debug("'{}' => await load skin", request.identifier);
                var semaphore = new Semaphore(0);
                delegate.listen((skin, exception) -> semaphore.release());
                semaphore.acquire();
                ModLog.debug("'{}' => await load skin completed", request.identifier);
            } catch (Exception exception) {
                ModLog.debug("'{}' => await load skin failed", request.identifier, exception);
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

    private static abstract class LoadingSession extends Session {

        public LoadingSession(String name) {
            super(name);
        }

        @Override
        public Skin load(Request request) throws Exception {
            try (var inputStream = loadData(request.identifier)) {
                var startTime = System.currentTimeMillis();
                var skin = SkinSerializer.readFromStream(request.options, inputStream);
                var totalTime = System.currentTimeMillis() - startTime;
                loadDidFinish(request, skin, totalTime);
                return skin;
            }
        }

        public abstract void loadDidFinish(Request request, Skin skin, long loadTime);

        public abstract InputStream loadData(String identifier) throws Exception;
    }

    private static class LocalDataSession extends LoadingSession {

        public LocalDataSession() {
            super("AW-SKIN-LD");
        }

        @Override
        public InputStream loadData(String identifier) throws Exception {
            // db:<skin-id>
            if (DataDomain.isDatabase(identifier)) {
                var id = DataDomain.getPath(identifier);
                return DataManager.getInstance().loadSkinData(id);
            }
            // fs:<file-path> or ws:<file-path>
            var path = FileUtils.normalize(DataDomain.getPath(identifier));
            if (path.isEmpty()) {
                throw new FileNotFoundException(identifier);
            }
            var file = new File(EnvironmentManager.getSkinLibraryDirectory(), path);
            if (file.exists()) {
                return new FileInputStream(file);
            }
            file = new File(file.getParent(), file.getName() + Constants.EXT);
            if (file.exists()) {
                return new FileInputStream(file);
            }
            throw new FileNotFoundException(identifier);
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from local session, time: {}ms", request.identifier, loadTime);
        }
    }

    private static class ResourcePackSession extends LoadingSession {

        public ResourcePackSession() {
            super("AW-SKIN-RP");
        }

        @Override
        public Skin load(Request request) throws Exception {
            // never get resource manager in dedicated server.
            if (EnvironmentManager.isDedicatedServer()) {
                throw new IllegalAccessException("the resource pack session only work in the client side.");
            }
            // fill security key when resource pack auth code provided.
            if (!ModConfig.Common.authCodeOfResourcePack.isEmpty()) {
                var key = DataAlgorithm.AUTH.key(ModConfig.Common.authCodeOfResourcePack);
                var options = new SkinFileOptions();
                options.setSecurityKey(key);
                options.setSecurityData(DataAlgorithm.AUTH.signature(key));
                request.options = options;
            }
            return super.load(request);
        }

        @Override
        public InputStream loadData(String identifier) throws Exception {
            // rs:<pack-id>:<skin-path>
            var path = DataDomain.getPath(identifier);
            if (path.isEmpty()) {
                throw new FileNotFoundException(identifier);
            }
            var file = OpenResourceLocation.parse(path);
            var resourceManager = EnvironmentManager.getClientResourceManager();
            if (resourceManager.hasResource(file)) {
                return resourceManager.readResource(file).inputStream();
            }
            // rs:<pack-id>:<skin-path>.armour
            file = file.withPath(file.path() + Constants.EXT);
            if (resourceManager.hasResource(file)) {
                return resourceManager.readResource(file).inputStream();
            }
            throw new FileNotFoundException(identifier);
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from resource pack session, time: {}ms", request.identifier, loadTime);
        }
    }

    private static class ProxySession extends Session {

        private final Semaphore available = new Semaphore(0, true);

        private final CacheSession caching = new CacheSession();

        public ProxySession() {
            super("AW-SKIN-PR");
        }

        @Override
        public void shutdown() {
            super.shutdown();
            this.caching.clean();
            this.caching.shutdown();
        }

        @Override
        public Skin load(Request request) throws Throwable {
            try {
                return caching.load(request);
            } catch (Exception ignored) {
            }
            ModLog.debug("'{}' => start request server skin", request.identifier);
            var req = new RequestSkinPacket(request.identifier);
            NetworkManager.sendToServer(req);
            return await(request);
        }

        public Skin await(Request request) throws Throwable {
            var state = new LockState(available);
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

        public void receive(Request request, LockState state, Skin skin, Throwable exception) {
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
            Throwable exception;

            LockState(Semaphore semaphore) {
                this.available = semaphore;
            }

            synchronized void timeout() {
                this.available = null;
            }

            synchronized void release() {
                var available = this.available;
                this.available = null;
                if (available != null) {
                    available.release();
                }
            }
        }
    }

    private static class DownloadSession extends LoadingSession {

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
        public InputStream loadData(String identifier) throws Exception {
            var domain = DataDomain.getNamespace(identifier);
            var loader = LOADER.loaders.get(domain);
            if (loader != null) {
                return loader.loadSkin(DataDomain.getPath(identifier));
            }
            throw new RuntimeException("can't support the '" + domain + "' protocol");
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from download session, time: {}ms", request.identifier, loadTime);
            caching.add(request.identifier, skin);
        }

        @Override
        protected ExecutorService buildThreadPool(String name, int size) {
            return super.buildThreadPool(name, 2);
        }
    }

    private static class CacheSession extends LoadingSession {

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
                    try {
                        FileUtils.forceMkdirParent(cachedFile);
                        if (cachedFile.exists()) {
                            FileUtils.deleteQuietly(cachedFile);
                        }
                        try (var outputStream = new FileOutputStream(cachedFile)) {
                            SkinSerializer.writeToStream(skin, null, outputStream);
                        }
                    } catch (Exception e) {
                        ModLog.error("'{}' => add global skin cache fail", identifier, e);
                    }
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
                    FileUtils.forceMkdirParent(cachedFile);
                    if (cachedFile.exists()) {
                        FileUtils.deleteQuietly(cachedFile);
                    }
                    fileOutputStream = new FileOutputStream(cachedFile);
                    if (x1.length != 0) {
                        fileOutputStream.write(x0);
                        SecretKeySpec key = new SecretKeySpec(x1, "AES");
                        Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        aes.init(Cipher.ENCRYPT_MODE, key);
                        cipherOutputStream = new CipherOutputStream(fileOutputStream, aes);
                        SkinSerializer.writeToStream(skin, null, cipherOutputStream);
                    }
                } catch (Exception e) {
                    ModLog.error("'{}' => add skin cache fail", identifier, e);
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
                FileUtils.deleteQuietly(cachedFile);
            });
        }

        @Override
        public InputStream loadData(String identifier) throws Exception {
            var cacheFile = cachingFile(identifier);
            if (cacheFile == null) {
                throw new FileNotFoundException(identifier);
            }
            // global data no need decrypt/encrypt
            if (isGlobalLibraryResource(identifier)) {
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
            FileUtils.setLastModifiedTime(cacheFile, System.currentTimeMillis());
            return new CipherInputStream(inputStream, aes);
        }

        @Override
        public void loadDidFinish(Request request, Skin skin, long loadTime) {
            ModLog.debug("'{}' => did load skin from cache session, time: {}ms", request.identifier, loadTime);
        }

        public void clean() {
            var t0 = ModContext.t0();
            if (t0 == null) {
                return;
            }
            // we need to clean it when the cache files haven’t been used for a long time.
            var expireTime = ModConfig.Common.skinCacheExpireTime;
            if (expireTime == 0) {
                return; // the user disable this features.
            }
            var expiredModifiedTime = System.currentTimeMillis() - (expireTime * 1000L);
            var rootPath = new File(EnvironmentManager.getSkinCacheDirectory(), t0.toString());
            ModLog.debug("clean skin cache");
            for (var cacheFile : FileUtils.listFilesRecursive(rootPath)) {
                if (cacheFile.isFile()) {
                    var lastModified = FileUtils.getLastModifiedTime(cacheFile);
                    if (lastModified < expiredModifiedTime) {
                        FileUtils.deleteQuietly(cacheFile);
                    }
                }
            }
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
                cacheFile = cachingFile(t0.toString(), namespace, Objects.md5(path));
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

    private static class SliceSession extends Session {

        public SliceSession() {
            super("AW-SKIN-SC");
        }

        @Override
        public Skin load(Request request) throws Exception {
            var parts = OpenCipher.getInstance().decrypt(DataDomain.getPath(request.identifier));
            if (parts.length != 2) {
                throw new RuntimeException("invalid identifier format!");
            }
            var id = parts[0];
            var keyPath = parts[1];
            var skin = LOADER.loadSkin(id);
            if (skin == null || !skin.settings().isEditable()) {
                throw new RuntimeException("can't load skin " + id);
            }
            var skinPart = extractPart(keyPath, skin.parts());
            if (skinPart == null) {
                throw new RuntimeException("can't load part " + keyPath + " in " + id);
            }
            var builder = new Skin.Builder(SkinTypes.ADVANCED);
            builder.paintData(skin.paintData());
            builder.version(skin.fileVersion());
            builder.parts(Collections.newList(skinPart));
            builder.settings(skin.settings().copy());
            builder.properties(skin.properties().copy());
            return builder.build();
        }

        SkinPart extractPart(String keyPath, List<SkinPart> parts) {
            SkinPart part = null;
            for (var key : keyPath.split("[.]")) {
                part = findPart(key, parts);
                if (part == null) {
                    return null;
                }
                parts = part.children();
            }
            return part;
        }

        SkinPart findPart(String key, List<SkinPart> parts) {
            for (var part : parts) {
                if (key.equals(part.name())) {
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
            var name = part.name();
            if (name != null && names.contains(name)) {
                return true;
            }
            for (var child : part.children()) {
                if (containsPart(names, child)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class TaskQueue {

        private boolean isPaused = true;
        private final ArrayList<Runnable> values = new ArrayList<>();

        public void pause() {
            isPaused = true;
        }

        public void resume() {
            isPaused = false;
            values.forEach(Runnable::run);
            values.clear();
        }

        public void submit(Runnable cmd) {
            if (isPaused) {
                values.add(cmd);
            } else {
                cmd.run();
            }
        }
    }
}
