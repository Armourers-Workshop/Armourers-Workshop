package riskyken.armourersWorkshop.common.tileentities;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnableChild;
import riskyken.armourersWorkshop.utils.ModLogger;

public class TileEntitySkinnableChild extends TileEntitySkinnable {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    
    public int parentX;
    public int parentY;
    public int parentZ;
    
    public boolean isParentValid() {
        return getParent() != null;
    }
    
    public void setParentLocation(int x, int y, int z) {
        parentX = x;
        parentY = y;
        parentZ = z;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void setBoundsOnBlock(Block block, int xOffset, int yOffset, int zOffset) {
        int x = xCoord - parentX;
        int y = yCoord - parentY;
        int z = zCoord - parentZ;
        
        int widthOffset = x;
        int heightOffset = y;
        int depthOffset = z;
        
        if (block != null && block instanceof BlockSkinnableChild) {
            ModLogger.log(Level.ERROR, String.format("Tile entity at X:%d Y:%d Z:%d has an invalid block.", xOffset, yOffset, zOffset));
            return;
        }
        
        BlockSkinnableChild child = (BlockSkinnableChild) getBlockType();
        ForgeDirection dir = child.getFacingDirection(getWorldObj(), xCoord, yCoord, zCoord);
        
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
        
        //ModLogger.log(depthOffset);
        
        
        super.setBoundsOnBlock(block, widthOffset, heightOffset, depthOffset);
    }
    
    @Override
    public TileEntitySkinnable getParent() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntitySkinnable) {
            return (TileEntitySkinnable) te;
        }
        return null;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        parentX = compound.getInteger(TAG_PARENT_X);
        parentY = compound.getInteger(TAG_PARENT_Y);
        parentZ = compound.getInteger(TAG_PARENT_Z);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, parentX);
        compound.setInteger(TAG_PARENT_Y, parentY);
        compound.setInteger(TAG_PARENT_Z, parentZ);
    }
}
