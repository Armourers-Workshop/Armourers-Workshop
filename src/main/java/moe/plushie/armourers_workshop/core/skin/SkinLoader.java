package moe.plushie.armourers_workshop.core.skin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.data.DataLoader;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.RequestFilePacket;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.client.Minecraft;
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

    private int queueCount = 1;
    private final ArrayList<LoadingTask> working = new ArrayList<>();
    private final ArrayList<LoadingTask> pending = new ArrayList<>();
    private final HashMap<SkinDescriptor, LoadingTask> tasks = new HashMap<>();
    private final DataLoader<SkinDescriptor, Skin> manager = DataLoader.newBuilder()
            .threadPool(1)
            .build(this::loadSkinFileIfNeeded);

    public static SkinLoader getInstance() {
        return LOADER;
    }

    @Nullable
    public Skin getSkin(ItemStack itemStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return null;
        }
        return getSkin(descriptor);
    }

    @Nullable
    public Skin getSkin(SkinDescriptor descriptor) {
        Optional<Skin> skin = manager.get(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    @Nullable
    public LoadingTask getTask(int id) {
        for (LoadingTask task : tasks.values()) {
            if (task.id == id) {
                return task;
            }
        }
        return null;
    }

    @Nullable
    public Skin loadSkin(SkinDescriptor descriptor) {
        Optional<Skin> skin = manager.getOrLoad(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    public void loadSkin(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> consumer) {
        manager.load(descriptor, false, consumer);
    }

    public SkinDescriptor cacheSkin(SkinDescriptor descriptor, Skin skin) {
        String identifier = descriptor.getIdentifier();
        if (identifier.startsWith("db:")) {
            return descriptor;
        }
        identifier = LocalDataService.getInstance().addFile(skin);
        if (identifier != null) {
            descriptor = new SkinDescriptor("db:" + identifier, descriptor.getType(), descriptor.getColorScheme());
            manager.put(descriptor, Optional.of(skin));
        }
        return descriptor;
    }

    public void clear() {
        manager.clear();
    }

    private void loadSkinFileIfNeeded(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> complete) {
        if (!Minecraft.getInstance().isLocalServer()) {
            addTask(descriptor, complete);
            return;
        }
        Optional<Skin> skin = loadSkinFile(descriptor, () -> DataManager.getInstance().loadSkinData(descriptor));
        if (complete != null) {
            complete.accept(skin);
        }
    }

    private Optional<Skin> loadSkinFile(SkinDescriptor descriptor, Supplier<Optional<ByteBuf>> buffer) {
        AWLog.debug("Parsing skin from data: {} ", descriptor);
        Optional<Skin> skin = buffer.get().map(buf1 -> SkinIOUtils.loadSkinFromStream(new ByteArrayInputStream(buf1.array())));
        manager.put(descriptor, skin);
        return skin;
    }

    private void addTask(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> complete) {
        LoadingTask task = tasks.computeIfAbsent(descriptor, LoadingTask::new);
        if (complete != null) {
            task.listeners.add(complete);
        }
        if (!working.contains(task)) {
            int index = pending.indexOf(task);
            if (index < 0) {
                AWLog.debug("Add loading task: {}", task.descriptor);
            }
            pending.add(0, task);

        }
        runTask();
    }

    private void finishTask(LoadingTask task) {
        AWLog.debug("Finish loading task: {}", task.descriptor);
        manager.add(() -> {
            ArrayList<Consumer<Optional<Skin>>> listeners = new ArrayList<>(task.listeners);
            ByteBuf buffer = Unpooled.buffer(task.receivedSize);
            task.receivedBuffers.forEach((index, buf) -> {
                buffer.writerIndex(index);
                buffer.writeBytes(buf);
            });
            task.clear();
            removeTask(task);
            Optional<Skin> skin = loadSkinFile(task.descriptor, () -> Optional.of(buffer));
            listeners.forEach(t -> t.accept(skin));
        });
    }

    private void removeTask(LoadingTask task) {
        AWLog.debug("Remove loading task: {}", task.descriptor);
        working.remove(task);
        pending.remove(task);
        tasks.remove(task.descriptor);
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
        AWLog.debug("Start loading task: {}", task.descriptor);
    }

    public class LoadingTask {

        private final int id;
        private final SkinDescriptor descriptor;
        private final ArrayList<Consumer<Optional<Skin>>> listeners = new ArrayList<>();

        private final HashMap<Integer, ByteBuf> receivedBuffers = new HashMap<>();
        private int receivedSize = 0;

        private boolean isRunning = false;
        private boolean isFinish = false;

        public LoadingTask(SkinDescriptor descriptor) {
            this.id = COUNTER.incrementAndGet();
            this.descriptor = descriptor;
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
            NetworkHandler.getInstance().sendToServer(new RequestFilePacket(id, descriptor));
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
            return id == task.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
