package riskyken.armourersWorkshop.client.equipment;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientRequestEquipmentDataData;
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
    
    private final HashMap<Integer, CustomEquipmentItemData> equipmentDataMap;
    private final HashSet<Integer> requestedEquipmentIds;
    
    public static void init() {
        INSTANCE = new ClientEquipmentModelCache();
    }
    
    public ClientEquipmentModelCache() {
        equipmentDataMap = new HashMap<Integer, CustomEquipmentItemData>();
        requestedEquipmentIds = new HashSet<Integer>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void requestEquipmentDataFromServer(int equipmentId) {
        if (!requestedEquipmentIds.contains(equipmentId)) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientRequestEquipmentDataData(equipmentId));
            requestedEquipmentIds.add(equipmentId);
        }
    }
    
    public boolean isEquipmentInCache(int equipmentId) {
        return equipmentDataMap.containsKey(equipmentId);
    }
    
    public void receivedEquipmentData(CustomEquipmentItemData equipmentData) {
        int equipmentId = equipmentData.hashCode();
        
        if (equipmentDataMap.containsKey(equipmentId)) {
            equipmentDataMap.remove(equipmentId);
        }
        for (int i = 0; i < equipmentData.getParts().size(); i++) {
            CustomEquipmentPartData partData = equipmentData.getParts().get(i);
            if (!partData.facesBuild) {
                EquipmentRenderHelper.cullFacesOnEquipmentPart(partData);
            }
        }
        equipmentDataMap.put(equipmentId, equipmentData);
        
        if (requestedEquipmentIds.contains(equipmentId)) {
            requestedEquipmentIds.remove(equipmentId);
        } else {
            ModLogger.log(Level.WARN, "Got an unknown equipment id: " + equipmentId);
        }
    }
    
    public int getCacheSize() {
        return equipmentDataMap.size();
    }
    
    public CustomEquipmentItemData getEquipmentItemData(int equipmentId) {
        if (equipmentDataMap.containsKey(equipmentId)) {
            return equipmentDataMap.get(equipmentId);
        } else {
            requestEquipmentDataFromServer(equipmentId);
            return null;
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.PLAYER & event.phase == Phase.END) {
            tick();
        }
    }
    
    public void tick() {
        for (int i = 0; i < equipmentDataMap.size(); i++) {
            int key = (Integer) equipmentDataMap.keySet().toArray()[i];
            equipmentDataMap.get(key).tick();
        }
        
        for (int i = 0; i < equipmentDataMap.size(); i++) {
            int key = (Integer) equipmentDataMap.keySet().toArray()[i];
            CustomEquipmentItemData customArmourItemData = equipmentDataMap.get(key);
            if (customArmourItemData.needsCleanup()) {
                equipmentDataMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
                break;
            }
        }
    }
}
