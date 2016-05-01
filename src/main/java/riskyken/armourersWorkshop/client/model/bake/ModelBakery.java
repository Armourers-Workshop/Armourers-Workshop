package riskyken.armourersWorkshop.client.model.bake;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureUtil;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public final class ModelBakery {

    public static final ModelBakery INSTANCE = new ModelBakery();
    
    /** Lock object use to keep threads in sync. */
    private final Object bakeLock = new Object();
    
    /** Number of model baking threads currently running. */
    private int runningOvens = 0;
    
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
        if (runningOvens >= ConfigHandler.maxModelBakingThreads & ConfigHandler.maxModelBakingThreads > 0) {
            return;
        }
        if (unbakedModels.size() == 0) {
            return;
        }
        Skin skin = unbakedModels.get(0);
        unbakedModels.remove(0);
        runningOvens++;
        Thread t = new Thread(new BakingOven(skin), LibModInfo.NAME + " model bake thread.");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
    
    private void dispatchBakery() {
        for (int i = 0; i < bakedModels.size(); i++) {
            Skin skin = bakedModels.get(i);
            if (skin.hasPaintData()) {
                skin.paintTextureId = TextureUtil.glGenTextures();
                TextureUtil.uploadTextureImage(skin.paintTextureId, skin.bufferedImage);
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
            
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPart partData = skin.getParts().get(i);
                partData.setClientSkinPartData(new ClientSkinPartData());
                SkinBaker.cullFacesOnEquipmentPart(partData);
                SkinBaker.buildPartDisplayListArray(partData);
                partData.clearCubeData();
            }
            
            if (skin.hasPaintData()) {
                ModLogger.log("baking skin paint");
                skin.bufferedImage = new BufferedImage(SkinTexture.TEXTURE_WIDTH, SkinTexture.TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
                    for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
                        int paintColour = skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
                        if (paintColour >>> 24 == 255) {
                            skin.bufferedImage.setRGB(ix, iy, skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)]);
                        }
                    }
                }
            }
            
            synchronized (bakeLock) {
                bakedModels.add(skin);
                runningOvens--;
            }
        }
    }
}
