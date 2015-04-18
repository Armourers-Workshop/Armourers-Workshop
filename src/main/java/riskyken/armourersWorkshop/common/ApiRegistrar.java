package riskyken.armourersWorkshop.common;

import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.common.handler.EquipmentDataHandler;
import riskyken.armourersWorkshop.utils.ModLogger;

import com.mojang.authlib.GameProfile;

public final class ApiRegistrar implements IEquipmentDataManager, IEquipmentRenderManager {
    
    public static final ApiRegistrar INSTANCE = new ApiRegistrar();
    
    public LinkedHashMap<String, IEquipmentDataManager> equipmentDataManagers = new LinkedHashMap<String, IEquipmentDataManager>();
    public LinkedHashMap<String, IEquipmentRenderManager> equipmentRenderManagers = new LinkedHashMap<String, IEquipmentRenderManager>();
    
    public void addApiRequest(String modName, String className) {
        try {
            
            Class<?> c = Class.forName(className);
            Object classObject = c.newInstance();
            if (classObject instanceof IEquipmentDataManager) {
                ModLogger.log(String.format("Loading %s API addon for %s", "data manager", modName));
                equipmentDataManagers.put(modName, ((IEquipmentDataManager)classObject));
                ((IEquipmentDataManager)classObject).onLoad(EquipmentDataHandler.INSTANCE);
            }
            if (classObject instanceof IEquipmentRenderManager) {
                ModLogger.log(String.format("Loading %s API addon for %s", "render manager", modName));
                equipmentRenderManagers.put(modName, ((IEquipmentRenderManager)classObject)) ;
                ((IEquipmentRenderManager)classObject).onLoad(EquipmentRenderHandler.INSTANCE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onLoad(IEquipmentRenderHandler handler) {}

    
    public void onRenderEquipment(Entity entity, ISkinType skinType) {
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

    @Override
    public void onRenderMannequin(TileEntity TileEntity, GameProfile gameProfile) {
        for (int i = 0; i < equipmentRenderManagers.size(); i++) {
            String key = (String) equipmentRenderManagers.keySet().toArray()[i];
            equipmentRenderManagers.get(key).onRenderMannequin(TileEntity, gameProfile);
        }
    }
}
