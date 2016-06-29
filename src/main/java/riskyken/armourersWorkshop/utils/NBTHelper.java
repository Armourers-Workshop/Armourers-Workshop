package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public final class NBTHelper {
    
    private static final String TAG_SLOT = "slot";
    private static final String TAG_BLOCK_POS = "blockPos";
    
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
                itemStacks[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }
    
    public static BlockPos readBlockPos(NBTTagCompound compound, String key) {
        if (compound.hasKey(TAG_BLOCK_POS + key, 11)) {
            int[] posArray = compound.getIntArray(TAG_BLOCK_POS + key);
            return new BlockPos(posArray[0], posArray[1], posArray[2]);
        }
        return new BlockPos(0, 0, 0);
    }
    
    public static void writeBlockPos(BlockPos pos, NBTTagCompound compound, String key) {
        int[] posArray = new int[] {pos.getX(), pos.getY(), pos.getZ()};
        compound.setIntArray(TAG_BLOCK_POS + key, posArray);
    }
}
