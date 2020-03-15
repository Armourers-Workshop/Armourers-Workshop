package moe.plushie.armourers_workshop.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class UtilItems {
    
    public static void spawnItemAtEntity(Entity entity, ItemStack stack, boolean pickup) {
        EntityItem entityitem = createEntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, stack);
        if (pickup) {
            entityitem.setNoPickupDelay();
            entityitem.setOwner(entity.getName());
        }
        entity.getEntityWorld().spawnEntity(entityitem);
    }
    
    public static void spawnItemInWorld(World world, Vec3d pos, ItemStack stack) {
        spawnItemInWorld(world, pos.x, pos.y, pos.z, stack);
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
        double xV = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double yV = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double zV = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        return new EntityItem(world, x + xV, y + yV, z + zV, stack);
    }
}
