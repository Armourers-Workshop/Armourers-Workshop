package moe.plushie.armourers_workshop.client.model.bake;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModelBakery {

    public static final ModelBakery INSTANCE = new ModelBakery();
    
    private final Executor skinBakeExecutor;
    private final Executor skinDownloadExecutor;
    private final CompletionService<BakedSkin> skinCompletion;
    private final AtomicInteger bakingQueue = new AtomicInteger(0);
    
    private final AtomicIntegerArray bakeTimes = new AtomicIntegerArray(1000);
    private final AtomicInteger bakeTimesIndex = new AtomicInteger(0);
    
    public ModelBakery() {
        skinBakeExecutor = Executors.newFixedThreadPool(ConfigHandlerClient.modelBakingThreadCount);
        skinDownloadExecutor = Executors.newFixedThreadPool(2);
        skinCompletion = new ExecutorCompletionService<BakedSkin>(skinBakeExecutor);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            checkBakery();
        }
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
        return (int) ((double)totalTime / (double)totalItems);
    }
    
    public void receivedUnbakedModel(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
        bakingQueue.incrementAndGet();
        skinCompletion.submit(new BakingOven(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver));
    }
    
    private void checkBakery() {
        Future<BakedSkin> futureSkin = skinCompletion.poll();
        while (futureSkin != null) {
            try {
                BakedSkin bakedSkin = futureSkin.get();
                if (bakedSkin != null) {
                    bakingQueue.decrementAndGet();
                    if (bakedSkin.skin == null) {
                        ModLogger.log(Level.ERROR, "A skin failed to bake.");
                    }
                    bakedSkin.getSkinReceiver().onBakedSkin(bakedSkin);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            futureSkin = skinCompletion.poll();
        }
    }
    
    public int getBakingQueueSize() {
        return bakingQueue.get();
    }
    
    public void handleModelDownload(Thread downloadThread) {
        downloadThread.setPriority(Thread.MIN_PRIORITY);
        skinDownloadExecutor.execute(downloadThread);
    }
    
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
        public BakedSkin call() throws Exception {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            if (skin == null) {
                return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
            }
            
            long startTime = System.currentTimeMillis();
            skin.lightHash();
            
            int extraDyes = PaintRegistry.getExtraChannels();
            
            int[][] dyeColour;
            int[] dyeUseCount;
            
            dyeColour = new int[3][extraDyes];
            dyeUseCount = new int[extraDyes];
            
            if (skin.hasPaintData()) {
                // TODO add new block for paint data.
            }
            
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPart partData = skin.getParts().get(i);
                partData.setClientSkinPartData(new ClientSkinPartData());
                int[][][] cubeArray = SkinBaker.cullFacesOnEquipmentPart(partData, ConfigHandlerClient.modelBakingUpdateRate.get());
                SkinBaker.buildPartDisplayListArray(partData, dyeColour, dyeUseCount, cubeArray, ConfigHandlerClient.modelBakingUpdateRate.get());
                partData.clearCubeData();
            }
            
            if (skin.hasPaintData()) {
                skin.skinModelTexture = new SkinModelTexture();
                for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
                    for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
                        int paintColour = skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
                        PaintType paintType = PaintRegistry.getPaintTypeFromColour(paintColour);
                        if (paintType.hasAverageColourChannel()) {
                            int index = paintType.getChannelIndex();
                            byte r = (byte) (paintColour >>> 16 & 0xFF);
                            byte g = (byte) (paintColour >>> 8 & 0xFF);
                            byte b = (byte) (paintColour & 0xFF);
                            dyeUseCount[index]++;
                            dyeColour[0][index] += r & 0xFF;
                            dyeColour[1][index] += g & 0xFF;
                            dyeColour[2][index] += b & 0xFF;
                        }
                    }
                }
            }
            
            int[] averageR = new int[extraDyes];
            int[] averageG = new int[extraDyes];
            int[] averageB = new int[extraDyes];
            
            for (int i = 0; i < extraDyes; i++) {
                averageR[i] = (int) ((double)dyeColour[0][i] / (double)dyeUseCount[i]);
                averageG[i] = (int) ((double)dyeColour[1][i] / (double)dyeUseCount[i]);
                averageB[i] = (int) ((double)dyeColour[2][i] / (double)dyeUseCount[i]);
            }
            
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPart partData = skin.getParts().get(i);
                partData.getClientSkinPartData().setAverageDyeValues(averageR, averageG, averageB);
            }
            
            skin.setAverageDyeValues(averageR, averageG, averageB);
            if (skin.hasPaintData()) {
                skin.skinModelTexture.createTextureForColours(skin, null);
            }
            long totalTime = System.currentTimeMillis() - startTime;
            int index = bakeTimesIndex.getAndIncrement();
            if (index > bakeTimes.length() - 1) {
                index = 0;
                bakeTimesIndex.set(0);
            }
            bakeTimes.set(index, (int)totalTime);
            return new BakedSkin(skin, skinIdentifierRequested, skinIdentifierUpdated, skinReceiver);
        }
    }
    
    public static class BakedSkin {
        
        private final Skin skin;
        private final SkinIdentifier skinIdentifierRequested;
        private final SkinIdentifier skinIdentifierUpdated;
        private final IBakedSkinReceiver skinReceiver;
        
        public BakedSkin(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, IBakedSkinReceiver skinReceiver) {
            this.skin = skin;
            this.skinIdentifierRequested = skinIdentifierRequested;
            this.skinIdentifierUpdated = skinIdentifierUpdated;
            this.skinReceiver = skinReceiver;
        }

        public Skin getSkin() {
            return skin;
        }

        public SkinIdentifier getSkinIdentifierRequested() {
            return skinIdentifierRequested;
        }

        public SkinIdentifier getSkinIdentifierUpdated() {
            return skinIdentifierUpdated;
        }
        
        public IBakedSkinReceiver getSkinReceiver() {
            return skinReceiver;
        }
    }
}
