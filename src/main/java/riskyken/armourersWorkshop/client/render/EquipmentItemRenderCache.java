package riskyken.armourersWorkshop.client.render;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelCustomItemBuilt;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientRequestEquipmentDataData;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Holds a cache of ModelCustomItemBuilt that are used when the client renders a
 * item equipment model.
 * 
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class EquipmentItemRenderCache {
    
    private static HashMap<Integer, ModelCustomItemBuilt> modelCache = new HashMap<Integer, ModelCustomItemBuilt>();
    private static HashSet<Integer> requestedEquipmentIds = new HashSet<Integer>();
    
    public static boolean isEquipmentInCache(int equipmentId) {
        return modelCache.containsKey(equipmentId);
    }
    
    public static void requestEquipmentDataFromServer(int equipmentId) {
        if (!requestedEquipmentIds.contains(equipmentId)) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientRequestEquipmentDataData(equipmentId));
            requestedEquipmentIds.add(equipmentId);
        }
    }
    
    public static void receivedEquipmentData(CustomArmourItemData equipmentData) {
        int equipmentId = equipmentData.hashCode();
        
        ModLogger.log(Level.INFO, "Got equipment data for id: " + equipmentId);
        NBTTagCompound armourNBT = new NBTTagCompound();
        equipmentData.writeToNBT(armourNBT);
        buildModelForCache(armourNBT, equipmentData.getType(), equipmentId);
        if (requestedEquipmentIds.contains(equipmentId)) {
            requestedEquipmentIds.remove(equipmentId);
        } else {
            ModLogger.log(Level.WARN, "Got an unknown equipment id: " + equipmentId);
        }
    }
    
    public static void renderItemAsArmourModel(ItemStack stack) {
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUPMENT_ID);
        
        if (!modelCache.containsKey(equipmentId)) {
            requestEquipmentDataFromServer(equipmentId);
            return;
        }
        
        if (!modelCache.containsKey(equipmentId)) {
            //buildModelForCache(armourNBT, ArmourType.getOrdinal(stack.getItemDamage() + 1), equipmentId);
        }
        
        ModelCustomItemBuilt targetModel = modelCache.get(equipmentId);
        
        if (targetModel == null) {
            ModLogger.log(Level.ERROR, "Model was not found in the model cache. Something is very wrong.");
            return;
        }
        //ModLogger.log(Level.INFO, "Rendering model: " + targetModel.);
        
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
    
    public static int getCacheSize() {
        return modelCache.size();
    }
    
    private static synchronized void buildModelForCache(NBTTagCompound armourNBT, ArmourType armourType, int key) {
        CustomArmourItemData itemData = new CustomArmourItemData(armourNBT);
        ModelCustomItemBuilt newModel = new ModelCustomItemBuilt(itemData, armourType, key);
        if (modelCache.containsKey(key)) {
            modelCache.remove(key);
        }
        modelCache.put(key, newModel);
    }
    
    private static synchronized void removeModelFromCache(int key) {
        ModelCustomItemBuilt removeModel = modelCache.get(key);
        if (removeModel != null) {
            modelCache.remove(key);
            removeModel.cleanUp();
        }
    }

    public static void tick() {
        for (int i = 0; i < modelCache.size(); i++) {
            int key = (Integer) modelCache.keySet().toArray()[i];
            modelCache.get(key).tick();
        }
        
        for (int i = 0; i < modelCache.size(); i++) {
            int key = (Integer) modelCache.keySet().toArray()[i];
            if (modelCache.get(key).needsCleanup()) {
                removeModelFromCache(key);
                break;
            }
        }
    }
}
