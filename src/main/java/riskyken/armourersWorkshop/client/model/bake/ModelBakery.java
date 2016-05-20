package riskyken.armourersWorkshop.client.model.bake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.utils.BitwiseUtils;

@SideOnly(Side.CLIENT)
public final class ModelBakery {

    public static final ModelBakery INSTANCE = new ModelBakery();
    
    /** Lock object use to keep threads in sync. */
    private final Object bakeLock = new Object();
    
    /** Number of model baking threads currently running. */
    private AtomicInteger runningOvens = new AtomicInteger(0);
    
    /** List of models that still need to be baked. */
    private final ArrayList<Skin> unbakedModels = new ArrayList<Skin>();
    
    /** Models that have been baked and should be send to the model cache. */
    private final ArrayList<Skin> bakedModels = new ArrayList<Skin>();
    
    public ModelBakery() {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            checkBakery();
        }
    }
    
    public void receivedUnbakedModel(Skin skin) {
        synchronized (bakeLock) {
            unbakedModels.add(skin);
        }
    }
    
    private void checkBakery() {
        synchronized (bakeLock) {
            loadOvens();
            dispatchBakery();
        }
    }
    
    private void loadOvens() {
        if (runningOvens.get() >= ConfigHandler.maxModelBakingThreads & ConfigHandler.maxModelBakingThreads > 0) {
            return;
        }
        if (unbakedModels.size() == 0) {
            return;
        }
        Skin skin = unbakedModels.get(0);
        unbakedModels.remove(0);
        runningOvens.incrementAndGet();
        Thread t = new Thread(new BakingOven(skin), LibModInfo.NAME + " model bake thread.");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
    
    private void dispatchBakery() {
        for (int i = 0; i < bakedModels.size(); i++) {
            Skin skin = bakedModels.get(i);
            if (skin.hasPaintData()) {
                try {
                    skin.skinModelTexture.loadTexture(Minecraft.getMinecraft().getResourceManager());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ClientSkinCache.INSTANCE.receivedModelFromBakery(skin);
        }
        bakedModels.clear();
    }
    
    public int getBakingQueueSize() {
        synchronized (bakeLock) {
            return unbakedModels.size();
        }
    }
    
    private class BakingOven implements Runnable {

        private final Skin skin;
        
        public BakingOven(Skin skin) {
            this.skin = skin;
        }
        
        @Override
        public void run() {
            skin.lightHash();
            
            int[][] dyeColour;
            int[] dyeUseCount;
            
            dyeColour = new int[3][10];
            dyeUseCount = new int[10];
            
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPart partData = skin.getParts().get(i);
                partData.setClientSkinPartData(new ClientSkinPartData());
                int[][][] cubeArray = SkinBaker.cullFacesOnEquipmentPart(partData);
                SkinBaker.buildPartDisplayListArray(partData, dyeColour, dyeUseCount, cubeArray);
                partData.clearCubeData();
            }
            
            if (skin.hasPaintData()) {
                skin.skinModelTexture = new SkinModelTexture();
                for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
                    for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
                        int paintColour = skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
                        int paintType = BitwiseUtils.getUByteFromInt(paintColour, 0);
                        
                        byte r = (byte) (paintColour >>> 16 & 0xFF);
                        byte g = (byte) (paintColour >>> 8 & 0xFF);
                        byte b = (byte) (paintColour & 0xFF);
                        
                        if (paintType >= 1 && paintType <= 8) {
                            dyeUseCount[paintType - 1]++;
                            dyeColour[0][paintType - 1] += r & 0xFF;
                            dyeColour[1][paintType - 1] += g & 0xFF;
                            dyeColour[2][paintType - 1] += b & 0xFF;
                        }
                        if (paintType == 253) {
                            dyeUseCount[8]++;
                            dyeColour[0][8] += r & 0xFF;
                            dyeColour[1][8] += g & 0xFF;
                            dyeColour[2][8] += b & 0xFF;
                        }
                        if (paintType == 254) {
                            dyeUseCount[9]++;
                            dyeColour[0][9] += r  & 0xFF;
                            dyeColour[1][9] += g  & 0xFF;
                            dyeColour[2][9] += b  & 0xFF;
                        }
                    }
                }
            }
            
            int[] averageR = new int[10];
            int[] averageG = new int[10];
            int[] averageB = new int[10];
            
            for (int i = 0; i < 10; i++) {
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
            
            synchronized (bakeLock) {
                bakedModels.add(skin);
                runningOvens.decrementAndGet();
            }
        }
    }
}
