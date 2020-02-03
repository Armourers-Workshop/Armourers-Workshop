package moe.plushie.armourers_workshop.common.tileentities;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.common.init.blocks.BlockSkinnable;
import moe.plushie.armourers_workshop.common.init.blocks.BlockSkinnableChild;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntitySkinnableChild extends TileEntitySkinnable {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    
    private BlockPos parentPos;
    
    public boolean isParentValid() {
        return getParent() != null;
    }
    
    public void setParentLocation(BlockPos parentPos) {
        this.parentPos = parentPos;
        dirtySync();
    }
    
    @Override
    public AxisAlignedBB getBoundsForBlock(Block block, int xOffset, int yOffset, int zOffset) {
        if (parentPos == null) {
            parentPos = getPos();
        }
        int x = getPos().getX() - parentPos.getX();
        int y = getPos().getY() - parentPos.getY();
        int z = getPos().getZ() - parentPos.getZ();
        
        if (!isParentValid()) {
            return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }
        
        int widthOffset = x;
        int heightOffset = y;
        int depthOffset = z;
        
        if (block != null && !(block instanceof BlockSkinnableChild)) {
            ModLogger.log(Level.ERROR, String.format("Tile entity at X:%d Y:%d Z:%d has an invalid block.", xOffset, yOffset, zOffset));
            if (getWorld() != null) {
                //getWorld().removeTileEntity(offset);
            }
            return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }
        
        BlockSkinnableChild child = (BlockSkinnableChild) getBlockType();
        EnumFacing dir = world.getBlockState(getPos()).getValue(BlockSkinnable.STATE_FACING);
        
        switch (dir) {
        case NORTH:
            widthOffset = 1 - x;
            depthOffset = z;
            break;
        case EAST:
            widthOffset = -x;
            depthOffset = z + 1;
            break;
        case SOUTH:
            widthOffset = 1 - x;
            depthOffset = 2 + z;
            break;
        case WEST:
            widthOffset = 2 - x;
            depthOffset = z + 1;
            break;
        default:
            break;
        }
        
        return super.getBoundsForBlock(block, widthOffset, heightOffset, depthOffset);
    }
    
    @Override
    public TileEntitySkinnable getParent() {
        TileEntity te = getWorld().getTileEntity(parentPos);
        if (te != null && te instanceof TileEntitySkinnable) {
            return (TileEntitySkinnable) te;
        }
        return null;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        parentPos = new BlockPos(compound.getInteger(TAG_PARENT_X), compound.getInteger(TAG_PARENT_Y), compound.getInteger(TAG_PARENT_Z));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (parentPos != null) {
            compound.setInteger(TAG_PARENT_X, parentPos.getX());
            compound.setInteger(TAG_PARENT_Y, parentPos.getY());
            compound.setInteger(TAG_PARENT_Z, parentPos.getZ());
        }
        return compound;
    }
}
