package riskyken.armourersWorkshop.client.model.bake;

import java.util.ArrayList;

import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
            ClientModelCache.INSTANCE.receivedModelFromBakery(bakedModels.get(i));
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
                SkinBaker.cullFacesOnEquipmentPart(partData);
                SkinBaker.buildPartDisplayListArray(partData);
            }
            
            synchronized (bakeLock) {
                bakedModels.add(skin);
                runningOvens--;
            }
        }
    }
}
