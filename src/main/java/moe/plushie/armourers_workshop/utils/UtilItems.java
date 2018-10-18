package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.common.lib.LibCommonTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class UtilItems {
    
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
    
    public static void spawnItemAtEntity(Entity entity, ItemStack stack, boolean pickup) {
        EntityItem entityitem = createEntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, stack);
        if (pickup) {
            entityitem.setNoPickupDelay();
            entityitem.setOwner(entity.getName());
        }
        entity.getEntityWorld().spawnEntity(entityitem);
    }
    
    public static void spawnItemInWorld(World world, BlockPos pos, ItemStack stack) {
        spawnItemInWorld(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
    
    public static void spawnItemInWorld(World world, double x, double y, double z, ItemStack stack) {
        EntityItem entityitem = createEntityItem(world, x, y, z, stack);
        world.spawnEntity(entityitem);
    }
    
    private static EntityItem createEntityItem(World world, double x, double y, double z, ItemStack stack) {
        float f = 0.7F;
        double xV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double yV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double zV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        return new EntityItem(world, x + xV, y + yV, z + zV, stack);
    }
}
