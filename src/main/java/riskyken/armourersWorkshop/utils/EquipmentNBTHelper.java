package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.items.ModItems;

public class EquipmentNBTHelper {
    
    public static boolean itemStackHasCustomEquipment(ItemStack stack) {
        NBTTagCompound itemNbt = NBTHelper.getNBTForStack(stack);
        
        if (!itemNbt.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return false;}
        NBTTagCompound armourNBT = itemNbt.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) { return false; }
        return true;
    }
    
    public static int getEquipmentIdFromStack(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        if (!stack.hasTagCompound()) {
            return 0;
        }
        NBTTagCompound itemNbt = stack.getTagCompound();
        
        if (!itemNbt.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return 0;}
        NBTTagCompound armourNBT = itemNbt.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) { return 0; }
        
        return armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
    }
    
    public static ItemStack makeStackForEquipment(CustomEquipmentItemData armourItemData) {
        ItemStack stack = new ItemStack(ModItems.equipmentSkin, 1, armourItemData.getType().ordinal() - 1);
        
        NBTTagCompound itemNBT = new NBTTagCompound();
        NBTTagCompound armourNBT = new NBTTagCompound();
        
        armourItemData.writeClientDataToNBT(armourNBT);
        EquipmentDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
        itemNBT.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourNBT);
        
        stack.setTagCompound(itemNBT);
        
        return stack;
    }
    
    public static void addRenderIdToStack(ItemStack stack, int equipmentId) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                //The stack already has a render id. Check if it needs updated.
                NBTTagCompound armourData = compound.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
                int newId = equipmentId;
                int oldId = armourData.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
                if (newId != oldId) {
                    armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, newId);
                    compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
                }
            } else {
                //The stack has NBT but no render id.
                NBTTagCompound armourData = new NBTTagCompound();
                armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, equipmentId);
                compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
            }
        } else {
            //The stack has no NBT so just add the render id.
            stack.setTagCompound(new NBTTagCompound());
            NBTTagCompound compound = stack.getTagCompound();
            NBTTagCompound armourData = new NBTTagCompound();
            armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, equipmentId);
            compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
        }
    }
    
    public static void removeRenderIdToStack(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                compound.removeTag(LibCommonTags.TAG_ARMOUR_DATA);
            }
        }
    }
}
