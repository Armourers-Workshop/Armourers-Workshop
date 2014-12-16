package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper {

    public static NBTTagCompound getNBTForStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
    
    public static boolean stackHasKey(ItemStack stack, String key) {
        if (stack == null) {
            return false;
        }
        if (!stack.hasTagCompound()) {
            return false;
        }
        if (stack.getTagCompound().hasKey(key)) {
            return true;
        }
        return false;
    }
}
