package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;

public class PaintingNBTHelper {
    
    public static boolean getToolHasColour(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(LibCommonTags.TAG_COLOUR)) {
            return true;
        }
        return false;
    }
    
    public static int getToolColour(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(LibCommonTags.TAG_COLOUR)) {
            return compound.getInteger(LibCommonTags.TAG_COLOUR);
        }
        return 16777215;
    }
    
    public static void setToolColour(ItemStack stack, int colour) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        stack.setTagCompound(compound);
    }
}
