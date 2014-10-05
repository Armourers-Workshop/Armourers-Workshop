package riskyken.armourersWorkshop.common.customEquipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;

public class EquipmentNBTHelper {
    
    public static boolean itemStackHasCustomEquipment(ItemStack stack) {
        if (!stack.hasTagCompound()) { return false; }
        NBTTagCompound itemNbt = stack.getTagCompound();
        
        if (!itemNbt.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return false;}
        NBTTagCompound armourNBT = itemNbt.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) { return false; }
        return true;
    }
    
    public static int getEquipmentIdFromStack(ItemStack stack) {
        if (!stack.hasTagCompound()) { return 0; }
        NBTTagCompound itemNbt = stack.getTagCompound();
        
        if (!itemNbt.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return 0;}
        NBTTagCompound armourNBT = itemNbt.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) { return 0; }
        
        return armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
    }
}
