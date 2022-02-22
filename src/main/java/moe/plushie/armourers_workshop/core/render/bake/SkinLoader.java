package moe.plushie.armourers_workshop.core.render.bake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.RequestFilePacket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.DataLoader;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class SkinLoader {

    private final static AtomicInteger COUNTER = new AtomicInteger();
    static HashMap<String, String> PATHS = new HashMap<>();

    static {
        String[] paths = {
                "护摩之杖.armour",
                "胡桃.armour",
                "钟离.armour",
                "浊心斯卡蒂+海嗣背饰.armour",
                "12531 - 早柚.armour",
                "12740 - V1 Wings.armour",
                "T.armour",
                "T-SW.armour",
                "T-RH.armour",
                "TR-H.armour",
                "T2-H.armour",
                "T2.armour",
                "11152 - Kagutsuchi Overlay (The Fire God).armour",
                "9265 - Luke's Droid Shovel.armour",
                "12072 - PINK PICKAXE.armour",
                "10293 - Garry's mod Tool gun.armour",
                "12162 - Energized Pickaxe.armour",
                "12661 - Arcane Jayce Mercury Hammer - LoL.armour",
                "10564 - Rose Glass Shield.armour",
                "12626 - 飞雷之弦振.armour",
                "12418 - [Random] - Starlight Axe.armour",
                "12729 - White Bat Ears.armour",
                "12902 - Winter Before.armour",
                "12414 - Komi.armour",
                "10032 - ?.armour",
                "2/5818 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ1.armour",
                "2/5819 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ2.armour",
                "2/5820 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ3.armour",
                "2/5821 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ4.armour",
                "2/6390 - å†°ä¸Žç\u0081«ä¹‹æ\u00ADŒ.armour",
                "2/6397 - é—ªè€€è“\u009Dé“\u0081ä¹‹å‰‘.armour",
                "2/6462 - ç¬¦æ–‡-æµ\u0081ç\u0081«.armour",
                "2/6463 - ç¬¦æ–‡-å‡\u009Déœœ.armour",
                "12388 - Light rifle (Halo).armour",
                "T/T-H.armour",
                "T/T-C.armour",
                "T/T-F.armour",
                "T/T-L.armour",
                "T/T-W.armour",
                "TP/TP-H.armour",
                "TP/TP-C.armour",
                "TP/TP-F.armour",
                "TP/TP-L.armour",
                "TP/TP-W.armour",
        };
        for (int i = 0; i < paths.length; ++i) {
            PATHS.put(String.valueOf(i), "./armoures/" + paths[i]);
        }
    }

    private final int queueCount = 1;
    private final ArrayList<LoadingTask> working = new ArrayList<>();
    private final ArrayList<LoadingTask> pending = new ArrayList<>();
    private final HashMap<SkinDescriptor, LoadingTask> tasks = new HashMap<>();
    private final DataLoader<SkinDescriptor, Skin> manager = DataLoader.newBuilder()
            .threadPool(1)
            .build(this::addTask);

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

    private void addTask(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> complete) {
        if (Minecraft.getInstance().isLocalServer()) {
            loadSkinFile(descriptor, complete);
            return;
        }
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
            Optional<Skin> skin = parseSkinFile(task.descriptor, buffer);
            manager.put(task.descriptor, skin);
            AWLog.debug("Notify loading task: {}", task.descriptor);
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

    private Optional<Skin> parseSkinFile(SkinDescriptor descriptor, ByteBuf buf) {
        AWLog.debug("Parsing skin from data: {} ", descriptor);
        sleep();
        ByteArrayInputStream nf = new ByteArrayInputStream(buf.array());
        Skin skin = SkinIOUtils.loadSkinFromStream(nf);
        return Optional.ofNullable(skin);
    }

    public void loadSkinData(SkinDescriptor descriptor, Consumer<Optional<ByteBuf>> consumer) {
        AWLog.debug("Load skin data: {} ", descriptor);
        manager.add(() -> {
            InputStream stream = loadStreamFromPath(descriptor);
            if (stream == null) {
                consumer.accept(Optional.empty());
                return;
            }
            try {
                sleep();
                int size = stream.available();
                ByteBuf buf = Unpooled.buffer(size);
                buf.writeBytes(stream, size);
                buf.resetReaderIndex();
                consumer.accept(Optional.of(buf));

            } catch (IOException e) {
                consumer.accept(Optional.empty());
            }
        });
    }

    private void loadSkinFile(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> complete) {
        Skin skin = loadSkinFromPath(descriptor);
        if (complete != null) {
            complete.accept(Optional.ofNullable(skin));
        }
    }

    private void sleep() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


//        new WorldSavedData("xxx") {
//            @Override
//            public void load(CompoundNBT p_76184_1_) {
//                p_76184_1_.getString("Key");
//                p_76184_1_.getByteArray("Value");
//            }
//
//            @Override
//            public CompoundNBT save(CompoundNBT p_189551_1_) {
//                p_189551_1_.putString("Key", "???");
//                p_189551_1_.putByteArray("Value", ...);
//                return null;
//            }
//        };


    }

    @Nullable
    private InputStream loadStreamFromPath(SkinDescriptor descriptor) {
        String identifier = descriptor.getIdentifier();
        if (identifier.isEmpty()) {
            return null;
        }
        String path = PATHS.get(identifier);
        if (path == null) {
            return null;
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Nullable
    private Skin loadSkinFromPath(SkinDescriptor descriptor) {
        String identifier = descriptor.getIdentifier();
        if (identifier.isEmpty()) {
            return null;
        }
        String path = PATHS.get(identifier);
        if (path == null) {
            return null;
        }
        Skin skin = SkinIOUtils.loadSkinFromFile(new File(path));
        AWLog.debug("Loading skin " + descriptor + " did complete !");
        return skin;
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
