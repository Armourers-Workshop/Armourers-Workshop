package riskyken.armourersWorkshop.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.blocks.BlockColourable;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;
import riskyken.plushieWrapper.common.world.BlockLocation;

public final class BlockUtils {
    
    private BlockUtils() {}
    
    public static int determineOrientation(World world, int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - (double) entity.yOffset;

            if (d0 - (double) y > 2.0D) { return 1; }
            if ((double) y - d0 > 0.0D) { return 0; }
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int determineOrientationSide(World world, int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    public static int getColourFromTileEntity(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour(side);
        }
        return UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT);
    }
    
    public static ICubeColour getColourFromTileEntity(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour();
        }
        return new CubeColour();
    }
    
    public static void dropInventoryBlocks(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IInventory) {
            dropInventoryBlocks(world, (IInventory)te, x, y, z);
        }
    }
    
    public static void dropInventoryBlocks(World world, IInventory inventory, int x, int y, int z) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                UtilItems.spawnItemInWorld(world, x, y, z, stack);
            }
        }
    }
    
    public static ArrayList<BlockLocation> findTouchingBlockFaces(World world, int x, int y, int z, int side, int radius) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        ArrayList<BlockLocation> blockFaces = new ArrayList<BlockLocation>();
        ArrayList<BlockLocation> openList = new ArrayList<BlockLocation>();
        ArrayList<BlockLocation> closedList = new ArrayList<BlockLocation>();
        
        openList.add(new BlockLocation(x, y ,z).offset(dir));
        ForgeDirection[] sides = ForgeDirection.VALID_DIRECTIONS;
        
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
        
        return blockFaces;
    }
    
    public static class BlockPath {
        
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
