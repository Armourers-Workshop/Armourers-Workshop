package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

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
        super.setBoundsOnBlock(block, 1, 0, 0);
    }
    
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
