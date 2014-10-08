package riskyken.armourersWorkshop.common;

import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.common.handler.EquipmentDataHandler;
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
            equipmentDataManagers.get(key).onLoad(EquipmentDataHandler.INSTANCE);
        }
        
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            ModLogger.log(String.format("Loading %s for API addon for %s", "render manager", key));
            equipmentRenderManagers.get(key).onLoad(EquipmentRenderHandler.INSTANCE);
        }
    }
    
    @Override
    public void onLoad(IEquipmentRenderHandler handler) {}

    @Override
    public void onRenderEquipment(Entity entity, EnumEquipmentType armourType) {
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            equipmentRenderManagers.get(key).onRenderEquipment(entity, armourType);
        }
    }

    @Override
    public void onRenderEquipmentPart(Entity entity, EnumEquipmentPart armourPart) {
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            equipmentRenderManagers.get(key).onRenderEquipmentPart(entity, armourPart);
        }
    }

    @Override
    public void onLoad(IEquipmentDataHandler dataHandler) {
    }
}
