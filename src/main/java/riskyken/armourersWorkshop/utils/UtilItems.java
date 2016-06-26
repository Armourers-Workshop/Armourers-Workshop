package riskyken.armourersWorkshop.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.plushieWrapper.common.item.PlushieItemStack;

public final class UtilItems {

    public static int getIntensityFromStack(PlushieItemStack stack, int defaultValue) {
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
    
    public static void spawnItemInWorld(World world, BlockPos pos, ItemStack stack) {
        float f = 0.7F;
        double xV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double yV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double zV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, (double)pos.getX() + xV, (double)pos.getY() + yV, (double)pos.getZ() + zV, stack);
        world.spawnEntityInWorld(entityitem);
    }
}
