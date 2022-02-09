package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.skin.data.*;
import moe.plushie.armourers_workshop.core.utils.DataLoader;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class SkinBakery {


    //    private final Executor skinBakeExecutor;
//    private final Executor skinDownloadExecutor;
//    private final CompletionService<BakedSkin> skinCompletion;
    private final AtomicInteger bakingQueue = new AtomicInteger(0);
    private final AtomicIntegerArray bakeTimes = new AtomicIntegerArray(1000);


    private final DataLoader<SkinDescriptor, BakedSkin> manager = DataLoader.newBuilder()
            .build(this::loadAndBakeSkin);


    public Object addListener(Consumer<SkinDescriptor> listener) {
        return listener;
    }

    public void removeListener(Object lister) {
    }


    //    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
//            checkBakery();
//        }
//    }
    private final AtomicInteger bakeTimesIndex = new AtomicInteger(0);

    public SkinBakery() {
//        skinBakeExecutor = Executors.newFixedThreadPool(ConfigHandlerClient.modelBakingThreadCount);
//        skinDownloadExecutor = Executors.newFixedThreadPool(2);
//        skinCompletion = new ExecutorCompletionService<BakedSkin>(skinBakeExecutor);
//        FMLCommonHandler.instance().bus().register(this);
    }

    @Nullable
    public BakedSkin getSkin(SkinDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        Optional<BakedSkin> skin = manager.get(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    @Nullable
    public BakedSkin loadSkin(SkinDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        Optional<BakedSkin> skin = manager.getOrLoad(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    public void loadSkin(SkinDescriptor descriptor, Consumer<Optional<BakedSkin>> consumer) {
        manager.load(descriptor, false, consumer);
    }

    private void loadAndBakeSkin(SkinDescriptor descriptor, Consumer<Optional<BakedSkin>> complete) {
        SkinCore.loader.loadSkin(descriptor, skin -> {
            Skin skin1 = skin.orElse(null);
            if (skin1 == null) {
                complete.accept(Optional.empty());
                return;
            }
            bakeSkin(skin1, descriptor, complete);
        });
    }

    private void bakeSkin(Skin skin, SkinDescriptor descriptor, Consumer<Optional<BakedSkin>> complete) {
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

        ArrayList<BakedSkinPart> bakedParts = new ArrayList<>();
        PackedColorInfo colorInfo = new PackedColorInfo();

        for (SkinPart part : skin.getParts()) {
            BakedSkinPart bakedPart = new BakedSkinPart(part, PackedQuad.from(part.getCubeData()));
            bakedParts.add(bakedPart);
            // part.clearCubeData();
        }

        for (Map.Entry<SkinTexture, PackedQuad> entry : PackedQuad.from(64, 32, skin.getPaintData()).entrySet()) {
            PackedQuad quads = entry.getValue();
            SkinPaintPart part = new SkinPaintPart(entry.getKey(), quads.getBounds(), quads.getRenderShape());
            BakedSkinPart bakedPart = new BakedSkinPart(part, quads);
            bakedParts.add(bakedPart);
        }

        for (int i = 0; i < bakedParts.size(); ++i) {
            BakedSkinPart bakedPart = bakedParts.get(i);
            bakedPart.setId(i);
            colorInfo.add(bakedPart.getColorInfo());
        }

//            for (int i = 0; i < skin.getParts().size(); i++) {
//                SkinPart partData = skin.getParts().get(i);
//                 partData.getClientSkinPartData().setAverageDyeValues(averageR, averageG,
//                 averageB);
//            }
//
//            skin.setAverageDyeValues(averageR, averageG, averageB);
        long totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);

        BakedSkin bakedSkin = new BakedSkin(skin, new SkinDye(), colorInfo, bakedParts);
        complete.accept(Optional.of(bakedSkin));

//            return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
//        }
//    }
    }

    public void getModel(ResourceLocation location) {

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

    public static interface IBakedSkinReceiver {
        public void onBakedSkin(BakedSkin bakedSkin);
    }


}
