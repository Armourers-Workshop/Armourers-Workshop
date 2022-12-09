package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.data.DataLoader;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.texture.TexturePart;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public final class SkinBakery {

    private static SkinBakery BAKERY;

    private final AtomicInteger bakingQueue = new AtomicInteger(0);
    private final AtomicIntegerArray bakeTimes = new AtomicIntegerArray(1000);

    private final ArrayList<IBakeListener> listeners = new ArrayList<>();

    private final DataLoader<String, BakedSkin> manager = DataLoader.newBuilder()
            .threadPool("AW-SKIN-BK", Thread.MIN_PRIORITY, ModConfig.Client.modelBakingThreadCount)
            .build(this::loadAndBakeSkin);

    public SkinBakery() {
//        skinBakeExecutor = Executors.newFixedThreadPool(ConfigHandlerClient.modelBakingThreadCount);
//        skinDownloadExecutor = Executors.newFixedThreadPool(2);
//        skinCompletion = new ExecutorCompletionService<BakedSkin>(skinBakeExecutor);
//        FMLCommonHandler.instance().bus().register(this);
    }

    @NotNull
    public static SkinBakery getInstance() {
        return Objects.requireNonNull(BAKERY, "bakery not start");
    }

    public static void start() {
        if (BAKERY == null) {
            BAKERY = new SkinBakery();
            ModLog.debug("start bakery");
        }
    }

    public static void stop() {
        if (BAKERY != null) {
            BAKERY.manager.clear();
            BAKERY = null;
            SkinVertexBufferBuilder.clearAllCache();
            ModLog.debug("stop bakery");
        }
    }

    public void addListener(IBakeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IBakeListener listener) {
        listeners.remove(listener);
    }

    @Nullable
    public BakedSkin getSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        Optional<BakedSkin> skin = manager.get(identifier);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    @Nullable
    public BakedSkin loadSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        Optional<BakedSkin> skin = manager.getOrLoad(identifier);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    public void loadSkin(String identifier, Consumer<Optional<BakedSkin>> consumer) {
        manager.load(identifier, true, consumer);
    }

    private void loadAndBakeSkin(String identifier, Consumer<Optional<BakedSkin>> complete) {
        SkinLoader.getInstance().loadSkin(identifier, (skin, exception) -> {
            if (skin != null) {
                manager.add(() -> safeBakeSkin(identifier, skin, complete));
            } else {
                complete.accept(Optional.empty());
            }
        });
    }

    private void safeBakeSkin(String identifier, Skin skin, Consumer<Optional<BakedSkin>> complete) {
        try {
            bakeSkin(identifier, skin, complete);
        } catch (Exception exception) {
            exception.printStackTrace();
            complete.accept(Optional.empty());
        }
    }

    private void bakeSkin(String identifier, Skin skin, Consumer<Optional<BakedSkin>> complete) {
        ModLog.debug("'{}' => start baking skin", identifier);
        long startTime = System.currentTimeMillis();
//            skin.lightHash();
//            int extraDyes = SkinPaintTypes.getInstance().getExtraChannels();
//
//            int[][] dyeColour;
//            int[] dyeUseCount;
//
//            dyeColour = new int[3][extraDyes];
//            dyeUseCount = new int[extraDyes];

//            if (ClientProxy.getTexturePaintType() == TexturePaintType.MODEL_REPLACE_AW) {
//                skin.addPaintDataParts();
//            }

        SkinUsedCounter usedCounter = new SkinUsedCounter();
        ArrayList<BakedSkinPart> bakedParts = new ArrayList<>();

        ColorScheme scheme = new ColorScheme();
        ColorDescriptor colorInfo = new ColorDescriptor();

        for (SkinPart part : skin.getParts()) {
            SkinCubeData data = part.getCubeData();
            BakedSkinPart bakedPart = new BakedSkinPart(part, PackedQuad.from(data));
            bakedParts.add(bakedPart);
            usedCounter.add(data.getUsedCounter());
            usedCounter.addFaceTotal(bakedPart.getFaceTotal());
            // part.clearCubeData();
        }

        for (Map.Entry<ISkinPartType, PackedQuad> entry : PackedQuad.from(skin.getPaintData()).entrySet()) {
            PackedQuad quads = entry.getValue();
            TexturePart part = new TexturePart(entry.getKey(), quads.getBounds(), quads.getRenderShape());
            BakedSkinPart bakedPart = new BakedSkinPart(part, quads);
            bakedParts.add(bakedPart);
        }

        int partId = 0;
        ArrayList<BakedSkinPart> iterator = new ArrayList<>(bakedParts);
        while (!iterator.isEmpty()) {
            BakedSkinPart bakedPart = iterator.remove(0);
            bakedPart.setId(partId++);
            colorInfo.add(bakedPart.getColorInfo());
            iterator.addAll(0, bakedPart.getChildren());
        }

        usedCounter.addPaints(colorInfo.getPaintTypes());

        long totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);

        BakedSkin bakedSkin = new BakedSkin(identifier, skin, scheme, usedCounter, colorInfo, bakedParts);
        ModLog.debug("'{}' => accept baked skin, time: {}ms", identifier, totalTime);
        complete.accept(Optional.of(bakedSkin));
        RenderSystem.recordRenderCall(() -> notifyBake(identifier, bakedSkin));

        // if bake speed too fast, cause system I/O too high.
        if (totalTime < 250) {
            sleep(100);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }

    private void notifyBake(String identifier, BakedSkin bakedSkin) {
        listeners.forEach(listener -> listener.didBake(identifier, bakedSkin));
    }

    public int getAverageBakeTime() {
        int totalItems = 0;
        int totalTime = 0;
        for (int i = 0; i < bakeTimes.length(); i++) {
            int time = bakeTimes.get(i);
            if (time != 0) {
                totalItems++;
                totalTime += time;
            }
        }
        return (int) ((double) totalTime / (double) totalItems);
    }

    public int getBakingQueueSize() {
        return bakingQueue.get();
    }

    @Environment(value = EnvType.CLIENT)
    @FunctionalInterface
    public interface IBakeListener {
        void didBake(String identifier, BakedSkin bakedSkin);
    }

//    public void handleModelDownload(Thread downloadThread) {
//        downloadThread.setPriority(Thread.MIN_PRIORITY);
//        skinDownloadExecutor.execute(downloadThread);
//    }
}
