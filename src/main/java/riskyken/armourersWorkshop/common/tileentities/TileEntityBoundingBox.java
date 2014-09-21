package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.BodyPart;

public class TileEntityBoundingBox extends TileEntity {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    private static final String TAG_BODY_PART = "bodyPart";
    
    private int parentX;
    private int parentY;
    private int parentZ;
    private BodyPart bodyPart;
    
    public TileEntityBoundingBox() {}
    
    public TileEntityBoundingBox(int parentX, int parentY, int parentZ, BodyPart bodyPart) {
        setParent(parentX, parentY, parentZ, bodyPart);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.parentX = compound.getInteger(TAG_PARENT_X);
        this.parentY = compound.getInteger(TAG_PARENT_Y);
        this.parentZ = compound.getInteger(TAG_PARENT_Z);
        this.bodyPart = BodyPart.values()[compound.getByte(TAG_BODY_PART)];
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, this.parentX);
        compound.setInteger(TAG_PARENT_Y, this.parentY);
        compound.setInteger(TAG_PARENT_Z, this.parentZ);
        compound.setByte(TAG_BODY_PART, (byte)this.bodyPart.ordinal());
    }
    
    public TileEntityArmourerBrain getParent() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourerBrain) {
            return (TileEntityArmourerBrain)te;
        }
        return null;
    }
    
    public BodyPart getBodyPart() {
        return bodyPart;
    }
    
    public void setParent(int x, int y, int z, BodyPart bodyPart) {
        this.parentX = x;
        this.parentY = y;
        this.parentZ = z;
        this.bodyPart = bodyPart;
        this.markDirty();
    }
}
