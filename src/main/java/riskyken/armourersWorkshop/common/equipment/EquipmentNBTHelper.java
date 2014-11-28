package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.items.ModItems;

public class EquipmentNBTHelper {
    
    public static boolean itemStackHasCustomEquipment(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound itemNbt = stack.getTagCompound();
        
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
    
    public static ItemStack makeStackForEquipmentId(int id, EnumEquipmentType equipmentType) {
        ItemStack stack = new ItemStack(ModItems.equipmentSkin, 1, equipmentType.ordinal() - 1);
        NBTTagCompound itemNbt = new NBTTagCompound();
        NBTTagCompound armourNBT = new NBTTagCompound();
        armourNBT.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, id);
        itemNbt.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourNBT);
        
        stack.setTagCompound(itemNbt);
        
        return stack;
    }
}
