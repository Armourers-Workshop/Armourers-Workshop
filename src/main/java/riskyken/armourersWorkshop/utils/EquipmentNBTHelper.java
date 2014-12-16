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
}
