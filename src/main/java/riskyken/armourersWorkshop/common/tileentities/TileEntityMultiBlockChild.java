package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMultiBlockChild extends TileEntity {

    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    private static final String TAG_HAS_PARENT = "hasParent";
    
    private int parentX;
    private int parentY;
    private int parentZ;
    private boolean hasParent;
    
    public TileEntityMultiBlockChild() {}
    
    public TileEntityMultiBlockChild(int parentX, int parentY, int parentZ) {
        setParent(parentX, parentY, parentZ);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        parentX = compound.getInteger(TAG_PARENT_X);
        parentY = compound.getInteger(TAG_PARENT_Y);
        parentZ = compound.getInteger(TAG_PARENT_Z);
        hasParent = compound.getBoolean(TAG_HAS_PARENT);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, parentX);
        compound.setInteger(TAG_PARENT_Y, parentY);
        compound.setInteger(TAG_PARENT_Z, parentZ);
        compound.setBoolean(TAG_HAS_PARENT, hasParent);
    }
    
    public void notifyParentOfChange() {
        if (!hasParent) { return; }
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourerBrain) {
            ((TileEntityArmourerBrain)te).childUpdate();
        }
    }
    
    public boolean getHasParent() {
        return hasParent;
    }
    
    public void setNoParent() {
        hasParent = false;
        markDirty();
    }
    
    public void setParent(int x, int y, int z) {
        hasParent = true;
        parentX = x;
        parentY = y;
        parentZ = z;
        markDirty();
    }
    
    public void clearParent(int x, int y, int z) {
        if (parentX != x) { return; }
        if (parentY != y) { return; }
        if (parentZ != z) { return; }
        setNoParent();
    }
}
