package riskyken.armourersWorkshop.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class UtilBlocks {
    public static int determineOrientation(World world, BlockPos pos, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) pos.getX()) < 2.0F && MathHelper.abs((float) entity.posZ - (float) pos.getZ()) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - (double) entity.getYOffset();

            if (d0 - (double) pos.getY() > 2.0D) { return 1; }
            if ((double) pos.getY() - d0 > 0.0D) { return 0; }
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int determineOrientationSide(World world, BlockPos pos, EntityLivingBase entity) {
        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int getColourFromTileEntity(World world, BlockPos pos, int side) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour(side);
        }
        return UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT);
    }
    
    public static ICubeColour getColourFromTileEntity(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour();
        }
        return new CubeColour();
    }
    
    public static void dropInventoryBlocks(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IInventory) {
            dropInventoryBlocks(world, (IInventory)te, pos);
        }
    }
    
    public static void dropInventoryBlocks(World world, IInventory inventory, BlockPos pos) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                UtilItems.spawnItemInWorld(world, pos, stack);
            }
        }
    }
}
