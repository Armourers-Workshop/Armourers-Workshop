package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public final class NBTHelper {
    
    private static final String TAG_SLOT = "slot";
    private static final String TAG_POS_X = "posX";
    private static final String TAG_POS_Y = "posY";
    private static final String TAG_POS_Z = "posZ";
    
    private NBTHelper() {}
    
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
    
    public static void writeStackArrayToNBT(NBTTagCompound compound, String key, ItemStack[] itemStacks) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack stack = itemStacks[i];
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte(TAG_SLOT, (byte)i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag(key, items);
    }
    
    public static void readStackArrayFromNBT(NBTTagCompound compound, String key, ItemStack[] itemStacks) {
        NBTTagList items = compound.getTagList(key, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)items.getCompoundTagAt(i);
            int slot = item.getByte(TAG_SLOT);
            if (slot >= 0 && slot < itemStacks.length) {
                //itemStacks[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }
    
    public static void writeBlockPosToNBT(NBTTagCompound compound, String key, BlockPos pos) {
        if (pos == null) {
            return;
        }
        compound.setInteger(TAG_POS_X + key, pos.getX());
        compound.setInteger(TAG_POS_Y + key, pos.getY());
        compound.setInteger(TAG_POS_Z + key, pos.getZ());
    }
    
    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound, String key) {
        return readBlockPosFromNBT(compound, key);
    }
    
    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound, String key, BlockPos defaultReturn) {
        if (compound.hasKey(TAG_POS_X + key, NBT.TAG_INT) & compound.hasKey(TAG_POS_Y + key, NBT.TAG_INT) & compound.hasKey(TAG_POS_Z + key, NBT.TAG_INT)) {
            return new BlockPos(compound.getInteger(TAG_POS_X + key), compound.getInteger(TAG_POS_Y + key), compound.getInteger(TAG_POS_Z + key));
        } else {
            return defaultReturn;
        }
    }
}
