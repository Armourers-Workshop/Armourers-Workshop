package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.*;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

@OnlyIn(Dist.CLIENT)
public final class ModelBakery {

    public static final ModelBakery INSTANCE = new ModelBakery();

    //    private final Executor skinBakeExecutor;
//    private final Executor skinDownloadExecutor;
//    private final CompletionService<BakedSkin> skinCompletion;
    private final AtomicInteger bakingQueue = new AtomicInteger(0);

    private final AtomicIntegerArray bakeTimes = new AtomicIntegerArray(1000);
    private final AtomicInteger bakeTimesIndex = new AtomicInteger(0);

    public ModelBakery() {
//        skinBakeExecutor = Executors.newFixedThreadPool(ConfigHandlerClient.modelBakingThreadCount);
//        skinDownloadExecutor = Executors.newFixedThreadPool(2);
//        skinCompletion = new ExecutorCompletionService<BakedSkin>(skinBakeExecutor);
//        FMLCommonHandler.instance().bus().register(this);
    }

//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
//            checkBakery();
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

    public void receivedUnbakedModel(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
//        bakingQueue.incrementAndGet();
//        skinCompletion.submit(new BakingOven(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver));
        try {
            BakingOven oven = new BakingOven(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
            oven.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BakedSkin backedModel(Skin skin) {
        BakingOven oven = new BakingOven(skin, null, null, null);
        return oven.call();
    }

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

    public int getBakingQueueSize() {
        return bakingQueue.get();
    }

//    public void handleModelDownload(Thread downloadThread) {
//        downloadThread.setPriority(Thread.MIN_PRIORITY);
//        skinDownloadExecutor.execute(downloadThread);
//    }

    public static interface IBakedSkinReceiver {
        public void onBakedSkin(BakedSkin bakedSkin);
    }

    private class BakingOven implements Callable<BakedSkin> {

        private final Skin skin;
        private final SkinIdentifier skinIdentifierRequested;
        private final SkinIdentifier skinIdentifierUpdated;
        private final IBakedSkinReceiver skinReceiver;

        public BakingOven(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
            this.skin = skin;
            this.skinIdentifierRequested = skinIdentifierRequested;
            this.skinIdentifierUpdated = skinIdentifierUpdated;
            this.skinReceiver = skinReceiver;
        }

        @Override
        public BakedSkin call() {
//            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            if (skin == null) {
                return null;
//                return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
            }

//            FastCache.INSTANCE.saveSkin(skin);

            long startTime = System.currentTimeMillis();
            skin.lightHash();

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

            PackedColorInfo colorInfo = new PackedColorInfo();

            for (SkinPart skinPart : skin.getParts()) {
//                int[][] partDyeColour = new int[3][extraDyes];
//                int[] partDyeUseCount = new int[extraDyes];


                PackedCube packedCube = new PackedCube(skinPart.getPartBounds(), skinPart.getCubeData());
                skinPart.setPackedFaces(packedCube.getFaces());
                skinPart.clearCubeData();

                colorInfo.add(packedCube.getColorInfo());

                skinPart.getQuads().colorInfo = packedCube.getColorInfo().optimize();
//                int[] partAverageR = new int[extraDyes];
//                int[] partAverageG = new int[extraDyes];
//                int[] partAverageB = new int[extraDyes];
//                for (int j = 0; j < extraDyes; j++) {
//                    partAverageR[j] = (int) ((double) partDyeColour[0][j] / (double) partDyeUseCount[j]);
//                    partAverageG[j] = (int) ((double) partDyeColour[1][j] / (double) partDyeUseCount[j]);
//                    partAverageB[j] = (int) ((double) partDyeColour[2][j] / (double) partDyeUseCount[j]);
//
//                    dyeColour[0][j] += partDyeColour[0][j];
//                    dyeColour[1][j] += partDyeColour[1][j];
//                    dyeColour[2][j] += partDyeColour[2][j];
//                    dyeUseCount[j] += partDyeUseCount[j];
//                }
//                partData.getClientSkinPartData().setAverageDyeValues(partAverageR, partAverageG, partAverageB);
            }

            if (skin.hasPaintData()) {
                int[] data = skin.getPaintData();
//                try {
////                    String a = "/Users/sagesse/Library/Application Support/minecraft/versions/1.16.5/1.16.5-assets/minecraft/textures/entity/alex.png";
//                    String a = "/Users/sagesse/Library/Application Support/minecraft/versions/1.16.5/1.16.5-assets/minecraft/textures/entity/steve.png";
////                    String a = "/Users/sagesse/Downloads/19248233.png";
////                    String a = "/Users/sagesse/Downloads/18355538.png";
//                    BufferedImage image = ImageIO.read(new File(a));
//                    int[] data2 = new int[64 * 64];
//                    image.getRGB(0, 0, 64, 64, data2, 0, 64);
//                    data = data2;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                int width = 64;
                int height = 32;
                int[] finalData = data;
                int partIndex = 0;
                ArrayList<SkinPart> paintParts = new ArrayList<>();
                for (SkinTexturedModel model : SkinTexturedModel.getPlayerModels(width, height, false)) {
                    PackedCubeFace faces = new PackedCubeFace();
                    model.forEach((u, v, x, y, z, dir) -> {
                        int c = finalData[v * width + u];
                        ISkinPaintType paintType = SkinPaintTypes.byId((c >> 24) & 0xFF);
                        if (paintType == SkinPaintTypes.NONE) {
                            return;
                        }
                        ColouredFace f = new ColouredFace(x, y, z, c, (byte) 255, (byte) 0, dir, SkinCubes.SOLID, paintType);
                        faces.add(f);
                    });
                    if (faces.getTotal() == 0) {
                        continue;
                    }
                    SkinPaintPart paintPart = new SkinPaintPart(model);
                    paintPart.setId(--partIndex);
                    paintPart.setPackedFaces(faces);
                    paintPart.setProperties(new SkinProperties());
                    paintParts.add(0, paintPart);
                }
                skin.getRenderParts().addAll(paintParts);
            }

            skin.colorInfo = colorInfo.optimize();

//
//            for (int i = 0; i < skin.getParts().size(); i++) {
//                SkinPart partData = skin.getParts().get(i);
//                 partData.getClientSkinPartData().setAverageDyeValues(averageR, averageG,
//                 averageB);
//            }
//
//            skin.setAverageDyeValues(averageR, averageG, averageB);
            long totalTime = System.currentTimeMillis() - startTime;
            int index = bakeTimesIndex.getAndIncrement();
            if (index > bakeTimes.length() - 1) {
                index = 0;
                bakeTimesIndex.set(0);
            }
            bakeTimes.set(index, (int) totalTime);

            return new BakedSkin(skin, new SkinDye());
//            return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
        }
    }

//    public static class BakedSkin {
//
//        private final Skin skin;
//        private final SkinIdentifier skinIdentifierRequested;
//        private final SkinIdentifier skinIdentifierUpdated;
//        private final IBakedSkinReceiver skinReceiver;
//
//        public BakedSkin(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
//            this.skin = skin;
//            this.skinIdentifierRequested = skinIdentifierRequested;
//            this.skinIdentifierUpdated = skinIdentifierUpdated;
//            this.skinReceiver = skinReceiver;
//        }
//
//        public Skin getSkin() {
//            return skin;
//        }
//
//        public SkinIdentifier getSkinIdentifierRequested() {
//            return skinIdentifierRequested;
//        }
//
//        public SkinIdentifier getSkinIdentifierUpdated() {
//            return skinIdentifierUpdated;
//        }
//
//        public IBakedSkinReceiver getSkinReceiver() {
//            return skinReceiver;
//        }
//    }
}
