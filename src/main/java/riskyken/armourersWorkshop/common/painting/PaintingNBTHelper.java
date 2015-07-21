package riskyken.armourersWorkshop.common.painting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;

public final class PaintingNBTHelper {
    
    private static final String TAG_FULL_BLOCK_MODE = "fullBlockMode";
    
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
    
    public static boolean isToolFullBlockMode(ItemStack stack) {
        return isToolFullBlockMode(stack.stackTagCompound);
    }
    
    public static boolean isToolFullBlockMode(NBTTagCompound compound) {
        if (compound != null && compound.hasKey(TAG_FULL_BLOCK_MODE)) {
            return compound.getBoolean(TAG_FULL_BLOCK_MODE);
        }
        return true;
    }
    
    public static void setToolFullBlockMode(ItemStack stack, boolean fullBlockMode) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        compound.setBoolean(TAG_FULL_BLOCK_MODE, fullBlockMode);
        stack.setTagCompound(compound);
    }
}
