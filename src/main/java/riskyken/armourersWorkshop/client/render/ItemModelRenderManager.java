package riskyken.armourersWorkshop.client.render;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelCustomItemBuilt;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemModelRenderManager {
    
    private static HashMap<String, ModelCustomItemBuilt> modelCache = new HashMap<String, ModelCustomItemBuilt>();
    
    public static void init() {
        
    }
    
    public static void renderItemAsArmourModel(ItemStack stack) {
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        String renderId = "-1";
        if (!armourNBT.hasKey(LibCommonTags.TAG_RENDER_ID)) {
            ModLogger.log(Level.WARN, "Item stack has no render ID. " + stack.getDisplayName());
            return;
        } else {
            renderId = armourNBT.getString(LibCommonTags.TAG_RENDER_ID);
        }
        
        if (!modelCache.containsKey(renderId)) {
            buildModelForCache(armourNBT, ArmourType.getOrdinal(stack.getItemDamage() + 1), renderId);
        }
        
        ModelCustomItemBuilt targetModel = modelCache.get(renderId);
        
        if (targetModel == null) {
            ModLogger.log(Level.ERROR, "Model was not found in the model cache. Something is very wrong.");
            return;
        }
        //CustomArmourItemData itemData = new CustomArmourItemData(armourNBT);
        
        switch (ArmourType.getOrdinal(stack.getItemDamage() + 1)) {
        case HEAD:
            GL11.glTranslatef(0F, 0.7F, 0F);
            targetModel.render();
            break;
        case CHEST:
            GL11.glTranslatef(0F, -0.35F, 0F);
            targetModel.render();
            break;
        case LEGS:
            GL11.glTranslatef(0F, -0.45F, 0F);
            targetModel.render();
            break;
        case SKIRT:
            GL11.glTranslatef(0F, -0.45F, 0F);
            targetModel.render();
            break;
        case FEET:
            GL11.glTranslatef(0F, -0.8F, 0F);
            targetModel.render();
            break;
        default:
            break;
        }
    }
    
    private static synchronized void buildModelForCache(NBTTagCompound armourNBT, ArmourType armourType, String key) {
        CustomArmourItemData itemData = new CustomArmourItemData(armourNBT);
        ModelCustomItemBuilt newModel = new ModelCustomItemBuilt(itemData, armourType, key);
        if (modelCache.containsKey(key)) {
            modelCache.remove(key);
        }
        ModLogger.log("Adding new model to cache");
        modelCache.put(key, newModel);
        ModLogger.log("Cache size " + modelCache.size());
    }
    
    private static synchronized void removeModelFromCache(String key) {
        ModLogger.log("Removing model from cache");
        ModelCustomItemBuilt removeModel = modelCache.get(key);
        if (removeModel != null) {
            modelCache.remove(key);
            removeModel.cleanUp();
        }
        ModLogger.log("Cache size " + modelCache.size());
    }

    public static void tick() {
        //ModLogger.log("tick");
        
        for (int i = 0; i < modelCache.size(); i++) {
            String key = (String) modelCache.keySet().toArray()[i];
            modelCache.get(key).tick();
        }
        
        for (int i = 0; i < modelCache.size(); i++) {
            String key = (String) modelCache.keySet().toArray()[i];
            if (modelCache.get(key).needsCleanup()) {
                removeModelFromCache(key);
                break;
            }
        }
    }
}
