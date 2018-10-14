package moe.plushie.armourers_workshop.utils;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public final class NBTHelper {
    
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
    
    public static void writeStackToNBT(NBTTagCompound compound, String key, ItemStack itemStack) {
        NBTTagCompound stackCompund = new NBTTagCompound();
        itemStack.writeToNBT(stackCompund);
        compound.setTag(key, stackCompund);
    }
    
    public static ItemStack readStackfromNBT(NBTTagCompound compound, String key) {
        ItemStack returnStack = ItemStack.EMPTY;
        if (compound.hasKey(key, NBT.TAG_COMPOUND)) {
            returnStack = new ItemStack(compound.getCompoundTag(key));
        }
        return returnStack;
    }
    
    public static void writeStackArrayToNBT(NBTTagCompound compound, String key,  NonNullList<ItemStack> itemStacks) {
        NBTTagCompound items = new NBTTagCompound();
        ItemStackHelper.saveAllItems(items, itemStacks);
        compound.setTag(key, items);
    }
    
    public static void readStackArrayFromNBT(NBTTagCompound compound, String key,  NonNullList<ItemStack> itemStacks) {
        NBTTagCompound items = compound.getCompoundTag(key);
        ItemStackHelper.loadAllItems(items, itemStacks);
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
        return readBlockPosFromNBT(compound, key, null);
    }
    
    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound, String key, BlockPos defaultReturn) {
        if (compound.hasKey(TAG_POS_X + key, NBT.TAG_INT) & compound.hasKey(TAG_POS_Y + key, NBT.TAG_INT) & compound.hasKey(TAG_POS_Z + key, NBT.TAG_INT)) {
            return new BlockPos(compound.getInteger(TAG_POS_X + key), compound.getInteger(TAG_POS_Y + key), compound.getInteger(TAG_POS_Z + key));
        } else {
            return defaultReturn;
        }
    }
}
