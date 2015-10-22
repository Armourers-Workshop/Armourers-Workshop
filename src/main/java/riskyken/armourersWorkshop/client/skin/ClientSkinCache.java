package riskyken.armourersWorkshop.client.skin;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class ClientSkinCache {
    
    public static ClientSkinCache INSTANCE;
    
    private final HashMap<Integer, Skin> equipmentDataMap;
    private final HashSet<Integer> requestedEquipmentIds;
    
    public static void init() {
        INSTANCE = new ClientSkinCache();
    }
    
    public ClientSkinCache() {
        equipmentDataMap = new HashMap<Integer, Skin>();
        requestedEquipmentIds = new HashSet<Integer>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void requestEquipmentDataFromServer(int equipmentId) {
        synchronized (requestedEquipmentIds) {
            if (!requestedEquipmentIds.contains(equipmentId)) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientRequestSkinData(equipmentId));
                requestedEquipmentIds.add(equipmentId);
            }
        }
    }
    
    public boolean isEquipmentInCache(int equipmentId) {
        synchronized (equipmentDataMap) {
            return equipmentDataMap.containsKey(equipmentId); 
        }
    }
    
    public void receivedModelFromBakery(Skin equipmentData) {
        int equipmentId = equipmentData.requestId;
        
        synchronized (requestedEquipmentIds) {
            synchronized (equipmentDataMap) {
                if (equipmentDataMap.containsKey(equipmentId)) {
                    Skin oldSkin = equipmentDataMap.get(equipmentId);
                    equipmentDataMap.remove(equipmentId);
                    oldSkin.cleanUpDisplayLists();
                    ModLogger.log("removing skin");
                }
            }
            
            if (requestedEquipmentIds.contains(equipmentId)) {
                synchronized (equipmentDataMap) {
                    equipmentDataMap.put(equipmentId, equipmentData);
                }
                requestedEquipmentIds.remove(equipmentId);
            } else {
                ModLogger.log(Level.WARN, "Got an unknown equipment id: " + equipmentId);
            }
        }
    }
    
    public int getCacheSize() {
        synchronized (equipmentDataMap) {
            return equipmentDataMap.size();
        }
    }
    
    public int getRequestQueueSize() {
        synchronized (requestedEquipmentIds) {
            return requestedEquipmentIds.size();
        }
    }
    
    public int getModelCount() {
        int count = 0;
        synchronized (equipmentDataMap) {
            Object[] keySet = equipmentDataMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = equipmentDataMap.get(key);
                count += skin.getModelCount();
            }
        }
        return count;
    }
    
    public int getPartCount() {
        int count = 0;
        synchronized (equipmentDataMap) {
            Object[] keySet = equipmentDataMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = equipmentDataMap.get(key);
                count += skin.getPartCount();
            }
        }
        return count;
    }
    
    public void clearCache() {
        synchronized (equipmentDataMap) {
            Object[] keySet = equipmentDataMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin customArmourItemData = equipmentDataMap.get(key);
                equipmentDataMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
            }
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
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            tick();
        }
    }
    
    public void tick() {
        synchronized (equipmentDataMap) {
            Object[] keySet = equipmentDataMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                equipmentDataMap.get(key).tick();
            }
            
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin customArmourItemData = equipmentDataMap.get(key);
                if (customArmourItemData.needsCleanup(ConfigHandler.clientModelCacheTime)) {
                    equipmentDataMap.remove(key);
                    customArmourItemData.cleanUpDisplayLists();
                    break;
                }
            }
        }
    }
}
