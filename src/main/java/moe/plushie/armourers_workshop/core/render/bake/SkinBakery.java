package moe.plushie.armourers_workshop.core.render.bake;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.DataLoader;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.texture.TexturePart;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.color.ColorDescriptor;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
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

    @Nonnull
    public static SkinBakery getInstance() {
        return Objects.requireNonNull(BAKERY);
    }

    public static void start() {
        if (BAKERY == null) {
            BAKERY = new SkinBakery();
        }
    }

    public static void stop() {
        if (BAKERY != null) {
            BAKERY.manager.clear();
            BAKERY = null;
            SkinVertexBufferBuilder.clearAllCache();
        }
    }


    public void addListener(IBakeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IBakeListener listener) {
        listeners.remove(listener);
    }

    @OnlyIn(Dist.CLIENT)
    @FunctionalInterface
    public interface IBakeListener {
        void didBake(String identifier, BakedSkin bakedSkin);
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

        for (Map.Entry<ISkinPartType, PackedQuad> entry : PackedQuad.from(64, 32, skin.getPaintData()).entrySet()) {
            PackedQuad quads = entry.getValue();
            TexturePart part = new TexturePart(entry.getKey(), quads.getBounds(), quads.getRenderShape());
            BakedSkinPart bakedPart = new BakedSkinPart(part, quads);
            bakedParts.add(bakedPart);
        }

        for (int i = 0; i < bakedParts.size(); ++i) {
            BakedSkinPart bakedPart = bakedParts.get(i);
            bakedPart.setId(i);
            colorInfo.add(bakedPart.getColorInfo());
        }

        usedCounter.addPaints(colorInfo.getPaintTypes());

        long totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);
//        try {
//            Thread.sleep(10000); // 10s
//        } catch (Exception e) {
//        }

        BakedSkin bakedSkin = new BakedSkin(identifier, skin, scheme, usedCounter, colorInfo, bakedParts);
        ModLog.debug("'{}' => accept baked skin", identifier);
        complete.accept(Optional.of(bakedSkin));
        RenderSystem.recordRenderCall(() -> notifyBake(identifier, bakedSkin));
    }

    private void notifyBake(String identifier, BakedSkin bakedSkin) {
        listeners.forEach(listener -> listener.didBake(identifier, bakedSkin));
    }

//    public void receivedUnbakedModel(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
////        bakingQueue.incrementAndGet();
////        skinCompletion.submit(new BakingOven(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver));
//        try {
//            BakingOven oven = new BakingOven(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
//            oven.call();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void loadAndBakeTexture(ResourceLocation texture, Consumer<Optional<BakedEntityTexture>> complete) {
//        BakedEntityTexture bakedTexture = new BakedEntityTexture(texture);
//        complete.accept(Optional.of(bakedTexture));
//    }

//    public BakedSkin backedModel(Skin skin) {
//        BakingOven oven = new BakingOven(skin, null, null, null);
//        return oven.call();
//    }


//
//    private void checkBakery() {
//        Future<BakedSkin> futureSkin = skinCompletion.poll();
//        while (futureSkin != null) {
//            try {
//                BakedSkin bakedSkin = futureSkin.get();
//                if (bakedSkin != null) {
//                    bakingQueue.decrementAndGet();
//                    if (bakedSkin.skin == null) {
//                        ModLogger.log(Level.ERROR, "A skin failed to bake.");
//                    }
//                    bakedSkin.getSkinReceiver().onBakedSkin(bakedSkin);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            futureSkin = skinCompletion.poll();
//        }
//    }

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

//    public void handleModelDownload(Thread downloadThread) {
//        downloadThread.setPriority(Thread.MIN_PRIORITY);
//        skinDownloadExecutor.execute(downloadThread);
//    }

    public int getBakingQueueSize() {
        return bakingQueue.get();
    }

//    private class BakingOven implements Callable<BakedSkin> {
//
//        private final Skin skin;
//        private final SkinIdentifier skinIdentifierRequested;
//        private final SkinIdentifier skinIdentifierUpdated;
//        private final IBakedSkinReceiver skinReceiver;
//
//        public BakingOven(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
//            this.skin = skin;
//            this.skinIdentifierRequested = skinIdentifierRequested;
//            this.skinIdentifierUpdated = skinIdentifierUpdated;
//            this.skinReceiver = skinReceiver;
//        }
//
//        @Override
//        public BakedSkin call() {
////            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//            if (skin == null) {
//                return null;
////                return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
//            }

//            FastCache.INSTANCE.saveSkin(skin);

}
