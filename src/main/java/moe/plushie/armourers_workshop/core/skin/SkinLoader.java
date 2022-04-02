package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.RequestSkinPacket;
import moe.plushie.armourers_workshop.core.utils.ResultHandler;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class SkinLoader {

    private static final SkinLoader LOADER = new SkinLoader();

    private final ArrayList<Entry> sendQueue = new ArrayList<>();
    private final ArrayList<Entry> responseQueue = new ArrayList<>();

    private final HashMap<String, Entry> entries = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(1, r -> {
        Thread thread = new Thread(r, "AW-Skin-Loader");
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    private long ticks = 0;


    public static SkinLoader getInstance() {
        return LOADER;
    }

    @Nullable
    public Skin getSkin(ItemStack itemStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
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
        Entry entry = getEntry(identifier);
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
        Entry entry = getOrCreateEntry(identifier);
        entry.load();
        return entry.get();
    }

    public void loadSkin(String identifier, @Nullable ResultHandler<Skin> handler) {
        Entry entry = getOrCreateEntry(identifier);
        entry.drink(handler);
        tick();
    }

    public String saveSkin(String identifier, Skin skin) {
        if (DataDomain.isDatabase(identifier)) {
            return identifier;
        }
        String newIdentifier = LocalDataService.getInstance().addFile(skin);
        if (newIdentifier != null) {
            identifier = DataDomain.DATABASE.normalize(newIdentifier);
            addSkin(identifier, skin);
        }
        return identifier;
    }


    public void addSkin(String identifier, Skin skin) {
        Entry entry = getOrCreateEntry(identifier);
        entry.accept(skin);
        tick();
    }

    public void addSkin(String identifier, InputStream inputStream, Exception exception) {
        Entry entry = getOrCreateEntry(identifier);
        executor.execute(() -> {
            if (inputStream != null) {
                entry.accept(inputStream);
            } else {
                entry.abort(exception);
            }
        });
    }

    public void removeSkin(String identifier) {
        Entry entry = entries.remove(identifier);
        if (entry != null && !entry.status.isCompleted()) {
            entry.abort(new CancellationException("removed by user"));
            removeEntry(entry);
        }
    }

    public void tick() {
        ticks = System.currentTimeMillis();
        executor.execute(() -> {
            tickLoad();
            tickTimeout();
            tickQueue();
        });
    }

    public void clear() {
        synchronized (this) {
            responseQueue.clear();
            sendQueue.clear();
            entries.clear();
        }
    }

    private void tickLoad() {
        ArrayList<Entry> entries;
        synchronized (this) {
            entries = new ArrayList<>(this.entries.values());
        }
        for (Entry entry : entries) {
            if (entry.status.isPending()) {
                entry.load();
            }
        }
    }

    private void tickTimeout() {
        ArrayList<Entry> entries;
        synchronized (this) {
            if (responseQueue.isEmpty()) {
                return;
            }
            entries = new ArrayList<>();
            int timeout = 60;
            for (Entry entry : responseQueue) {
                if ((entry.sendTicks - ticks) > timeout) {
                    entries.add(entry);
                }
            }
            entries.forEach(responseQueue::remove);
        }
        entries.forEach(e -> e.abort(new TimeoutException("long time to wait")));
    }

    private void tickQueue() {
        int queueSize = 1;
        synchronized (this) {
            if (sendQueue.isEmpty()) {
                return;
            }
            if (responseQueue.size() >= queueSize) {
                return;
            }
            sendQueue.sort(Entry::compareTo);
            while (!sendQueue.isEmpty()) {
                Entry entry = sendQueue.remove(sendQueue.size() - 1);
                if (entry.status.isLoading()) {
                    responseQueue.add(entry);
                    sendRequest(entry);
                    break;
                }
            }
        }
    }

    private Entry getEntry(String identifier) {
        synchronized (this) {
            return entries.get(identifier);
        }
    }

    private Entry getOrCreateEntry(String identifier) {
        synchronized (this) {
            return entries.computeIfAbsent(identifier, Entry::new);
        }
    }

    private void removeEntry(Entry entry) {
        synchronized (this) {
            responseQueue.remove(entry);
            sendQueue.remove(entry);
        }
        tick();
    }

    private void pendingEntry(Entry entry) {
        int queueSize = 0;
        synchronized (this) {
            sendQueue.add(entry);
            queueSize = sendQueue.size();
        }
        ModLog.debug("pending remote skin of {}, queue: {}", entry, queueSize);
    }

    private void sendRequest(Entry entry) {
        ModLog.debug("request remote skin of {}, queue: {}", entry, sendQueue.size());
        RequestSkinPacket req = new RequestSkinPacket(entry.identifier);
        NetworkHandler.getInstance().sendToServer(req);
        entry.sendTicks = System.currentTimeMillis();
    }


    public enum Status {
        PENDING, LOADING, FINISHED, CANCELLED, ABORTED;

        public boolean isPending() {
            return this == PENDING;
        }

        public boolean isLoading() {
            return this == LOADING;
        }

        public boolean isCompleted() {
            return this == FINISHED || this == ABORTED;
        }
    }

    public class Entry implements Comparable<Entry> {

        private final String identifier;
        private final ArrayList<ResultHandler<Skin>> handlers = new ArrayList<>();

        private Skin skin;
        private Status status;
        private Exception exception;

        private long ticks = 0;
        private long sendTicks = 0;

        public Entry(String identifier) {
            this.identifier = identifier;
            this.status = Status.PENDING;
        }

        public void tick() {
            this.ticks = System.currentTimeMillis();
        }

        public void load() {
            // accept only pending or cancelled task
            if (status.isCompleted() || status.isLoading()) {
                return;
            }
            ModLog.debug("load skin of {}", this);
            // when this is a remote file, add to the send queue
            if (!isLogicalServerSide() && !DataDomain.isLocal(identifier)) {
                // Load skin from cache database
                Skin cachedSkin = DataManager.getInstance().getCache(identifier);
                if (cachedSkin != null) {
                    ModLog.debug("accept cached skin of {}", this);
                    accept(cachedSkin);
                    return;
                }
                tick();
                status = Status.LOADING;
                pendingEntry(this);
                return;
            }
            // when this is a local file. we can load data them directly
            try {
                InputStream inputStream = DataManager.getInstance().loadSkinData3(identifier);
                ModLog.debug("accept local skin of {}", this);
                accept(SkinIOUtils.loadSkinFromStream(inputStream));
            } catch (Exception e) {
                abort(e);
            }
        }

        public void drain() {
            if (handlers.isEmpty()) {
                return;
            }
            // in case someone is added to the handlers again during the handler
            ArrayList<ResultHandler<Skin>> handlers1 = new ArrayList<>(handlers);
            handlers.clear();
            handlers1.forEach(handler -> handler.apply(skin, exception));
        }

        public void drink(ResultHandler<Skin> handler) {
            // when the load is completed, don't try again
            if (status.isCompleted()) {
                if (handler != null) {
                    handler.apply(skin, exception);
                }
                return;
            }
            // when the task is cancelled, it is re-pending
            if (status == Status.CANCELLED) {
                status = Status.PENDING;
            }
            // wait for the loader tick
            tick();
            if (handler != null) {
                handlers.add(handler);
            }
        }

        public void accept(InputStream stream) {
            ModLog.debug("accept skin stream of {}", this);
            try {
                Skin skin = SkinIOUtils.loadSkinFromStream(stream);
                accept(skin);
                stream.close();
                DataManager.getInstance().addCache(identifier, skin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void accept(Skin s) {
            sendTicks = 0;
            skin = s;
            status = Status.FINISHED;
            drain();
            removeEntry(this);
        }

        public void abort(Exception exp) {
            ModLog.debug("abort skin of {}, exception: {}", this, exp);
            exception = exp;
            status = Status.ABORTED;
            sendTicks = 0;
            // when load is time out, we'll load it again the next time
            if (exp instanceof TimeoutException || exp instanceof CancellationException) {
                status = Status.CANCELLED;
            }
            drain();
            removeEntry(this);
        }

        @Override
        public int compareTo(Entry o) {
            return Long.compare(ticks, o.ticks);
        }

        @Override
        public String toString() {
            return '\'' + identifier + '\'';
        }

        public boolean isLogicalServerSide() {
            if (FMLEnvironment.dist.isDedicatedServer()) {
                return true; // when is a physical server, always true regardless of whether local service is started
            }
            return LocalDataService.isRunning();
        }

        public Skin get() {
            if (!status.isCompleted()) {
                tick();
            }
            return skin;
        }
    }
}
