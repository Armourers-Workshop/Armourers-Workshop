package moe.plushie.armourers_workshop.core.skin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.data.DataLoader;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.data.FastCache;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.RequestFilePacket;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class SkinLoader {

    private static final SkinLoader LOADER = new SkinLoader();
    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final ArrayList<LoadingTask> working = new ArrayList<>();
    private final ArrayList<LoadingTask> pending = new ArrayList<>();

    private final HashMap<String, LoadingTask> tasks = new HashMap<>();

    private final FastCache cache = new FastCache();
    private final DataLoader<String, Skin> manager = DataLoader.newBuilder()
            .threadPool("AW-Skin-Loader", Thread.MIN_PRIORITY, 1)
            .build(this::loadSkinFileIfNeeded);

    private int queueCount = 1;

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
        Optional<Skin> skin = manager.get(identifier);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    @Nullable
    public LoadingTask getTask(int id) {
        for (LoadingTask task : tasks.values()) {
            if (task.seq == id) {
                return task;
            }
        }
        return null;
    }

    public String saveSkin(String identifier, Skin skin) {
        if (identifier.startsWith("db:")) {
            return identifier;
        }
        String newIdentifier = LocalDataService.getInstance().addFile(skin);
        if (newIdentifier != null) {
            identifier = "db:" + newIdentifier;
            manager.put(identifier, Optional.of(skin));
        }
        return identifier;
    }

    @Nullable
    public Skin loadSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        Optional<Skin> skin = manager.getOrLoad(identifier);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    public void loadSkin(String identifier, @Nullable Consumer<Optional<Skin>> consumer) {
        manager.load(identifier, false, consumer);
    }

    public void clear() {
        manager.clear();
    }

    private Optional<Skin> loadSkinFile(String identifier, Supplier<Optional<ByteBuf>> buffer) {
        ModLog.debug("load skin file: {}", identifier);
        Optional<Skin> skin = buffer.get().map(buf1 -> SkinIOUtils.loadSkinFromStream(new ByteArrayInputStream(buf1.array())));
        manager.put(identifier, skin);
        return skin;
    }

    private void loadSkinFileIfNeeded(String identifier, @Nullable Consumer<Optional<Skin>> complete) {
        boolean isLocalResource = identifier.startsWith("fs:");
        boolean isClientSide = !LocalDataService.isRunning();
        if (isClientSide && !isLocalResource) {
            loadRemoteSkinFile(identifier, complete);
            return;
        }
        Optional<Skin> skin = loadSkinFile(identifier, () -> DataManager.getInstance().loadSkinData(identifier));
        if (complete != null) {
            complete.accept(skin);
        }
    }
    private void loadRemoteSkinFile(String identifier, @Nullable Consumer<Optional<Skin>> complete) {
        ModLog.debug("load remote skin file: {}", identifier);
        addTask(identifier, complete);
    }

    private void addTask(String identifier, @Nullable Consumer<Optional<Skin>> complete) {
        LoadingTask task = tasks.computeIfAbsent(identifier, LoadingTask::new);
        if (complete != null) {
            task.listeners.add(complete);
        }
        if (!working.contains(task)) {
            int index = pending.indexOf(task);
            if (index < 0) {
                ModLog.debug("Add loading task: {}", task.identifier);
            }
            pending.add(0, task);

        }
        runTask();
    }

    private void finishTask(LoadingTask task) {
        ModLog.debug("Finish loading task: {}", task.identifier);
        manager.add(() -> {
            ArrayList<Consumer<Optional<Skin>>> listeners = new ArrayList<>(task.listeners);
            ByteBuf buffer = Unpooled.buffer(task.receivedSize);
            task.receivedBuffers.forEach((index, buf) -> {
                buffer.writerIndex(index);
                buffer.writeBytes(buf);
            });
            task.clear();
            removeTask(task);
            Optional<Skin> skin = loadSkinFile(task.identifier, () -> Optional.of(buffer));
            listeners.forEach(t -> t.accept(skin));
        });
    }

    private void removeTask(LoadingTask task) {
        ModLog.debug("Remove loading task: {}", task.identifier);
        working.remove(task);
        pending.remove(task);
        tasks.remove(task.identifier);
        task.clear();
        runTask();
    }

    private void runTask() {
        if (working.size() >= queueCount) {
            return;
        }
        if (pending.isEmpty()) {
            return;
        }
        LoadingTask task = pending.remove(0);
        working.add(task);
        task.resume();
        ModLog.debug("Start loading task: {}", task.identifier);
    }

    public class LoadingTask {

        private final int seq;
        private final String identifier;

        private final HashMap<Integer, ByteBuf> receivedBuffers = new HashMap<>();
        private final ArrayList<Consumer<Optional<Skin>>> listeners = new ArrayList<>();

        private boolean isRunning = false;
        private boolean isFinish = false;

        private int receivedSize = 0;

        public LoadingTask(String identifier) {
            this.seq = COUNTER.incrementAndGet();
            this.identifier = identifier;
        }

        public void append(int offset, int total, ByteBuf buf) {
            receivedSize += buf.readableBytes();
            receivedBuffers.put(offset, buf);
            if (receivedSize < total) {
                return;
            }
            isRunning = false;
            isFinish = true;
            finishTask(this);
        }

        public void resume() {
            if (isRunning || isFinish) {
                return;
            }
            isRunning = true;
            RequestFilePacket req = new RequestFilePacket(seq, identifier);
            NetworkHandler.getInstance().sendToServer(req);
        }

        public void clear() {
            isFinish = false;
            isRunning = false;
            listeners.clear();
            for (ByteBuf buf : receivedBuffers.values()) {
                buf.release();
            }
            receivedBuffers.clear();
            receivedSize = 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoadingTask task = (LoadingTask) o;
            return seq == task.seq;
        }

        @Override
        public int hashCode() {
            return seq;
        }
    }
}
