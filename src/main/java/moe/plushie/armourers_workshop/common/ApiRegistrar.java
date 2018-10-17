package moe.plushie.armourers_workshop.common;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.client.IArmourersClientManager;
import moe.plushie.armourers_workshop.api.common.IArmourersCommonManager;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.handler.EquipmentRenderHandler;
import moe.plushie.armourers_workshop.common.handler.SkinDataHandler;
import moe.plushie.armourers_workshop.common.skin.entity.EntitySkinHandler;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public final class ApiRegistrar {
    
    public static final ApiRegistrar INSTANCE = new ApiRegistrar();
    
    public LinkedHashMap<String, IArmourersCommonManager> equipmentDataManagers = new LinkedHashMap<String, IArmourersCommonManager>();
    public LinkedHashMap<String, IArmourersClientManager> equipmentRenderManagers = new LinkedHashMap<String, IArmourersClientManager>();
    
    public void addApiRequest(String modName, String className) {
        try {
            Class<?> c = Class.forName(className);
            Object classObject = c.newInstance();
            if (classObject instanceof IArmourersCommonManager) {
                ModLogger.log(String.format("Loading %s API addon for %s", "data manager", modName));
                equipmentDataManagers.put(modName, ((IArmourersCommonManager)classObject));
                ((IArmourersCommonManager)classObject).onLoad(SkinDataHandler.INSTANCE,
                        SkinTypeRegistry.INSTANCE, EntitySkinHandler.INSTANCE);
            }
            if (classObject instanceof IArmourersClientManager) {
                if (ArmourersWorkshop.isDedicated()) {
                    ModLogger.log(Level.ERROR, String.format("Mod %s is registering a render manager on the server side."
                            + " This is very bad!", modName));
                }
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
