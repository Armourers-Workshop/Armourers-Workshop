package moe.plushie.armourers_workshop.utils;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.blocks.BlockLocation;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import moe.plushie.armourers_workshop.utils.UtilColour.ColourFamily;
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
    
    public static int determineOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - (double) entity.getYOffset();

            if (d0 - (double) y > 2.0D) { return 1; }
            if ((double) y - d0 > 0.0D) { return 0; }
        }

        int l = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int determineOrientationSide(EntityLivingBase entity) {
        int rotation = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
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
    
    public static ArrayList<BlockLocation> findTouchingBlockFaces(World world, int x, int y, int z, int side, int radius) {
        EnumFacing dir = EnumFacing.byIndex(side);
        ArrayList<BlockLocation> blockFaces = new ArrayList<BlockLocation>();
        ArrayList<BlockLocation> openList = new ArrayList<BlockLocation>();
        ArrayList<BlockLocation> closedList = new ArrayList<BlockLocation>();
        /*
        openList.add(new BlockLocation(x, y ,z).offset(dir));
        EnumFacing[] sides = EnumFacing.VALUES;
        
        while (!openList.isEmpty()) {
            BlockLocation loc = openList.get(0);
            openList.remove(0);
            Block block = world.getBlock(loc.x, loc.y, loc.z);
            if (block instanceof BlockColourable) {
                blockFaces.add(loc);
            }
            if (world.isAirBlock(loc.x, loc.y, loc.z)) {
                for (int i = 0; i < sides.length; i++) {
                    BlockLocation sideLoc = loc.offset(sides[i]);
                    Block sideBlock = world.getBlock(sideLoc.x, sideLoc.y, sideLoc.z);
                    if (!closedList.contains(sideLoc)) {
                        closedList.add(sideLoc);
                        boolean validCube = false;
                        for (int ix = 0; ix < 3; ix++) {
                            for (int iy = 0; iy < 3; iy++) {
                                for (int iz = 0; iz < 3; iz++) {
                                    Block validBlock = world.getBlock(sideLoc.x + ix - 1, sideLoc.y + iy - 1, sideLoc.z + iz - 1);
                                    if (validBlock instanceof BlockColourable) {
                                        validCube = true;
                                    }
                                }
                            }
                        }
                        if (sideLoc.getDistance(x, y, z) < radius & validCube) {
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
        */
        return blockFaces;
    }
    
    public static class BlockFace extends BlockLocation {

        public final int face;
        
        public BlockFace(int x, int y, int z, int face) {
            super(x, y, z);
            this.face = face;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + face;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            BlockFace other = (BlockFace) obj;
            if (face != other.face)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "BlockFace [face=" + face + ", x=" + x + ", y=" + y + ", z=" + z + "]";
        }
    }
}
