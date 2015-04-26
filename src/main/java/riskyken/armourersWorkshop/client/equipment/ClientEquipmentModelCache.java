package riskyken.armourersWorkshop.client.equipment;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.client.model.bake.SkinBaker;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientRequestEquipmentDataData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEquipmentModelCache {
    
    public static ClientEquipmentModelCache INSTANCE;
    
    private final HashMap<Integer, Skin> equipmentDataMap;
    private final HashSet<Integer> requestedEquipmentIds;
    
    private static boolean isBaking = true;
    private static Object bakeLock = new Object();
    
    public static void init() {
        INSTANCE = new ClientEquipmentModelCache();
    }
    
    public ClientEquipmentModelCache() {
        equipmentDataMap = new HashMap<Integer, Skin>();
        requestedEquipmentIds = new HashSet<Integer>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void requestEquipmentDataFromServer(int equipmentId) {
        synchronized (requestedEquipmentIds) {
            if (!requestedEquipmentIds.contains(equipmentId)) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientRequestEquipmentDataData(equipmentId));
                requestedEquipmentIds.add(equipmentId);
            }
        }
    }
    
    public boolean isEquipmentInCache(int equipmentId) {
        synchronized (equipmentDataMap) {
            return equipmentDataMap.containsKey(equipmentId); 
        }
    }
    
    public void receivedEquipmentData(Skin equipmentData) {
        int equipmentId = equipmentData.hashCode();
        
        synchronized (equipmentDataMap) {
            if (equipmentDataMap.containsKey(equipmentId)) {
                equipmentDataMap.remove(equipmentId);
            }
        }
        
        Thread t = (new Thread(new FaceCullThread(equipmentData, equipmentId),LibModInfo.NAME + " model bake thread."));
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
    
    public int getCacheSize() {
        synchronized (equipmentDataMap) {
            return equipmentDataMap.size();
        }
    }
    
    public void clearCache() {
        synchronized (equipmentDataMap) {
            equipmentDataMap.clear();
        }
    }
    
    public Skin getEquipmentItemData(int equipmentId) {
        synchronized (equipmentDataMap) {
            if (equipmentDataMap.containsKey(equipmentId)) {
                return equipmentDataMap.get(equipmentId);
            }
        }
        requestEquipmentDataFromServer(equipmentId);
        return null;
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.PLAYER & event.phase == Phase.END) {
            tick();
        }
    }
    
    public void tick() {
        synchronized (equipmentDataMap) {
            for (int i = 0; i < equipmentDataMap.size(); i++) {
                int key = (Integer) equipmentDataMap.keySet().toArray()[i];
                equipmentDataMap.get(key).tick();
            }
            
            for (int i = 0; i < equipmentDataMap.size(); i++) {
                int key = (Integer) equipmentDataMap.keySet().toArray()[i];
                Skin customArmourItemData = equipmentDataMap.get(key);
                if (customArmourItemData.needsCleanup()) {
                    equipmentDataMap.remove(key);
                    customArmourItemData.cleanUpDisplayLists();
                    break;
                }
            }
        }
    }
    
    public class FaceCullThread implements Runnable {
        
        private Skin equipmentData;
        private int equipmentId;
        
        public FaceCullThread(Skin equipmentData, int equipmentId) {
            this.equipmentData = equipmentData;
            this.equipmentId = equipmentId;
        }
        
        @Override
        public void run() {
            
            synchronized (bakeLock) {
                isBaking = true;
                equipmentData.lightHash();
                for (int i = 0; i < equipmentData.getParts().size(); i++) {
                    SkinPart partData = equipmentData.getParts().get(i);
                    if (!partData.facesBuild) {
                        SkinBaker.cullFacesOnEquipmentPart(partData);
                        SkinBaker.buildPartDisplayListArray(partData);
                    }
                }
                isBaking = false;
            }
            
            synchronized (requestedEquipmentIds) {
                if (requestedEquipmentIds.contains(equipmentId)) {
                    synchronized (equipmentDataMap) {
                        equipmentDataMap.put(equipmentId, equipmentData);
                    }
                    requestedEquipmentIds.remove(equipmentId);
                } else {
                    //Out of date skin
                    if (requestedEquipmentIds.contains(equipmentData.requestId)) {
                        synchronized (equipmentDataMap) {
                            equipmentDataMap.put(equipmentData.requestId, equipmentData);
                        }
                        requestedEquipmentIds.remove(equipmentData.requestId);
                    } else {
                        ModLogger.log(Level.WARN, "Got an unknown equipment id: " + equipmentId);
                    }
                }
            }
            
            
        }
    }
}
