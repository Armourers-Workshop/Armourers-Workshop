package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.common.equipment.EnumBodyPart;

public class TileEntityBoundingBox extends TileEntity {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    private static final String TAG_BODY_PART = "bodyPart";
    
    private int parentX;
    private int parentY;
    private int parentZ;
    private EnumBodyPart bodyPart;
    
    public TileEntityBoundingBox() {
        bodyPart = EnumBodyPart.CHEST;
    }
    
    public TileEntityBoundingBox(int parentX, int parentY, int parentZ, EnumBodyPart bodyPart) {
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
        this.bodyPart = EnumBodyPart.values()[compound.getByte(TAG_BODY_PART)];
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, this.parentX);
        compound.setInteger(TAG_PARENT_Y, this.parentY);
        compound.setInteger(TAG_PARENT_Z, this.parentZ);
        compound.setByte(TAG_BODY_PART, (byte)this.bodyPart.ordinal());
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public TileEntityArmourerBrain getParent() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourerBrain) {
            return (TileEntityArmourerBrain)te;
        }
        return null;
    }
    
    public EnumBodyPart getBodyPart() {
        return bodyPart;
    }
    
    public void setParent(int x, int y, int z, EnumBodyPart bodyPart) {
        this.parentX = x;
        this.parentY = y;
        this.parentZ = z;
        this.bodyPart = bodyPart;
        this.markDirty();
    }
}
