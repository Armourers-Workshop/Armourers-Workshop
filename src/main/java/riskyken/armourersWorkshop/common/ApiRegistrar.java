package riskyken.armourersWorkshop.common;

import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentCacheHandler;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentDataManager;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourPart;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;
import riskyken.armourersWorkshop.common.customEquipment.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.common.customEquipment.EquipmentDataCache;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class ApiRegistrar implements IEquipmentDataManager, IEquipmentRenderManager {
    
    public static final ApiRegistrar INSTANCE = new ApiRegistrar();
    
    public LinkedHashMap<String, IEquipmentDataManager> equipmentDataManagers = new LinkedHashMap<String, IEquipmentDataManager>();
    public LinkedHashMap<String, IEquipmentRenderManager> equipmentRenderManagers = new LinkedHashMap<String, IEquipmentRenderManager>();
    
    public void addApiRequest(String modName, String className) {
        try {
            Class<?> c = Class.forName(className);
            Object classObject = c.newInstance();
            if (classObject instanceof IEquipmentDataManager) {
                equipmentDataManagers.put(modName, ((IEquipmentDataManager)classObject)) ;
            }
            if (classObject instanceof IEquipmentRenderManager) {
                equipmentRenderManagers.put(modName, ((IEquipmentRenderManager)classObject)) ;
            }
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onLoad() {
        int addonCount = equipmentDataManagers.size() + equipmentRenderManagers.size();
        if (addonCount > 0) {
            ModLogger.log(String.format("Loading %s API addons.", addonCount));
        } else {
            ModLogger.log("No API addons to load.");
        }
        
        for (int i = 0; i < equipmentDataManagers.size(); i++) {
            String key = (String) equipmentDataManagers.keySet().toArray()[i];
            ModLogger.log(String.format("Loading %s for API addon for %s", "data manager", key));
            equipmentDataManagers.get(key).onLoad(EntityEquipmentDataManager.INSTANCE, EquipmentDataCache.INSTANCE);
        }
        
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            ModLogger.log(String.format("Loading %s for API addon for %s", "render manager", key));
            equipmentRenderManagers.get(key).onLoad(EquipmentPlayerRenderCache.INSTANCE);
        }
    }
    
    @Override
    public void onLoad(IEquipmentRenderHandler handler) {}

    @Override
    public void onRenderEquipment(Entity entity, EnumArmourType armourType) {
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            equipmentRenderManagers.get(key).onRenderEquipment(entity, armourType);
        }
    }

    @Override
    public void onRenderEquipmentPart(Entity entity, EnumArmourPart armourPart) {
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            equipmentRenderManagers.get(key).onRenderEquipmentPart(entity, armourPart);
        }
    }

    @Override
    public void onLoad(IEquipmentDataHandler dataHandler, IEquipmentCacheHandler cacheHandler) {
    }
}
