package riskyken.armourersWorkshop.common;

import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.client.IArmourersClientManager;
import riskyken.armourersWorkshop.api.common.IArmourersCommonManager;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.handler.EquipmentDataHandler;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

import com.mojang.authlib.GameProfile;

public final class ApiRegistrar {
    
    public static final ApiRegistrar INSTANCE = new ApiRegistrar();
    
    public LinkedHashMap<String, IArmourersCommonManager> equipmentDataManagers = new LinkedHashMap<String, IArmourersCommonManager>();
    public LinkedHashMap<String, IArmourersClientManager> equipmentRenderManagers = new LinkedHashMap<String, IArmourersClientManager>();
    
    public void addApiRequest(String modName, String className) {
        if (!ConfigHandler.allowModsToRegisterWithAPI) {
            return;
        }
        try {
            Class<?> c = Class.forName(className);
            Object classObject = c.newInstance();
            if (classObject instanceof IArmourersCommonManager) {
                ModLogger.log(String.format("Loading %s API addon for %s", "data manager", modName));
                equipmentDataManagers.put(modName, ((IArmourersCommonManager)classObject));
                ((IArmourersCommonManager)classObject).onLoad(EquipmentDataHandler.INSTANCE,
                        SkinTypeRegistry.INSTANCE, EntitySkinHandler.INSTANCE);
            }
            if (classObject instanceof IArmourersClientManager) {
                ModLogger.log(String.format("Loading %s API addon for %s", "render manager", modName));
                equipmentRenderManagers.put(modName, ((IArmourersClientManager)classObject)) ;
                ((IArmourersClientManager)classObject).onLoad(EquipmentRenderHandler.INSTANCE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void onRenderEquipment(Entity entity, ISkinType skinType) {
    }
    
    public void onRenderEquipmentPart(Entity entity, ISkinPartType skinPart) {
    }
    
    public void onRenderMannequin(TileEntity TileEntity, GameProfile gameProfile) {
    }
}
