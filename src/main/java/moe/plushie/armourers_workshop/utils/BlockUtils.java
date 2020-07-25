package moe.plushie.armourers_workshop.utils;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class BlockUtils {
    
    private BlockUtils() {}
    
    public static int determineOrientation(BlockPos pos, EntityLivingBase entity) {
        return determineOrientation(pos.getX(), pos.getY(), pos.getZ(), entity);
    }
    
    public static int determineOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - x) < 2.0F && MathHelper.abs((float) entity.posZ - z) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - entity.getYOffset();

            if (d0 - y > 2.0D) { return 1; }
            if (y - d0 > 0.0D) { return 0; }
        }

        int l = MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int determineOrientationSide(EntityLivingBase entity) {
        int rotation = MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        rotation = determineOrientationSideMeta(rotation);
        return rotation;
    }
    
    public static int determineOrientationSideMeta(int metadata) {
        // up = 1
        // down = 0
        // north = 2 
        // south = 3
        // east = 5
        // west = 4
        return metadata == 0 ? 3 : (metadata == 3 ? 5 : (metadata == 1 ? 4 : 2));
    }
    
    public static EnumFacing determineDirectionSideMeta(int metadata) {
        return EnumFacing.byIndex(determineOrientationSideMeta(metadata));
    }
    
    public static EnumFacing determineDirectionSide(EntityLivingBase entity) {
        return EnumFacing.byIndex(determineOrientationSide(entity));
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
            ItemStack stack = inventory.removeStackFromSlot(i);
            if (stack != null) {
                UtilItems.spawnItemInWorld(world, pos, stack);
            }
        }
    }
    
    public static ArrayList<BlockPos> findTouchingBlockFaces(World world, BlockPos pos, EnumFacing facing, int radius, boolean restrictPlane) {
        ArrayList<BlockPos> blockFaces = new ArrayList<BlockPos>();
        ArrayList<BlockPos> openList = new ArrayList<BlockPos>();
        ArrayList<BlockPos> closedList = new ArrayList<BlockPos>();
        
        BlockPos startPos = pos.offset(facing);
        
        openList.add(pos.offset(facing));
        EnumFacing[] sides = EnumFacing.VALUES;
        
        boolean first = true;
        
        while (!openList.isEmpty()) {
            BlockPos loc = openList.get(0);
            openList.remove(0);
            IBlockState state = world.getBlockState(loc);
            if (state.getBlock() instanceof IPantableBlock) {
                if (restrictPlane) {
                    if (loc.getX() * facing.getXOffset() == pos.getX() * facing.getXOffset()) {
                        if (loc.getY() * facing.getYOffset() == pos.getY() * facing.getYOffset()) {
                            if (loc.getZ() * facing.getZOffset() == pos.getZ() * facing.getZOffset()) {
                                blockFaces.add(loc);
                            }
                        }
                    }
                } else {
                    blockFaces.add(loc);
                }
            }
            
            if (world.isAirBlock(loc)) {
                for (int i = 0; i < sides.length; i++) {
                    BlockPos sideLoc = loc.offset(EnumFacing.values()[i]);
                    IBlockState stateSide = world.getBlockState(sideLoc);
                    if (!closedList.contains(sideLoc)) {
                        closedList.add(sideLoc);
                        boolean validCube = false;

                        for (int ix = 0; ix < 3; ix++) {
                            for (int iy = 0; iy < 3; iy++) {
                                for (int iz = 0; iz < 3; iz++) {
                                    IBlockState stateValid = world.getBlockState(sideLoc.add(ix - 1, iy - 1, iz - 1));
                                    if (stateValid.getBlock() instanceof IPantableBlock) {
                                        validCube = true;

                                    }
                                }
                            }
                        }
                        
                        if (sideLoc.getDistance(pos.getX(), pos.getY(), pos.getZ()) < radius & validCube) {
                            openList.add(sideLoc);
                            //blocks.add(sideLoc);
                        }
                        
                    }
                }
            }
            if (closedList.size() > 5000) {
                break;
            }
        }
        
        return blockFaces;
    }
}
