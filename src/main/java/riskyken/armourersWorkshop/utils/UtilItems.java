package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;

public final class UtilItems {

    public static int getIntensityFromStack(ItemStackPointer stack, int defaultValue) {
        return getIntensityFromStack(stack.getMinecraftStack(), defaultValue);
    }
    
    public static int getIntensityFromStack(ItemStack stack, int defaultValue) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound stackNbt = stack.getTagCompound();
        if (!stackNbt.hasKey(LibCommonTags.TAG_INTENSITY)) {
            stackNbt.setInteger(LibCommonTags.TAG_INTENSITY, defaultValue);
        }
        return stackNbt.getInteger(LibCommonTags.TAG_INTENSITY);
    }
    
    public static void setIntensityOnStack(ItemStack stack, int intensity) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound stackNbt = stack.getTagCompound();
        stackNbt.setInteger(LibCommonTags.TAG_INTENSITY, intensity);
    }
}
