package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;

public class TileEntityBoundingBox extends TileEntity {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    private static final String TAG_SKIN_PART = "skinPart";
    
    private int parentX;
    private int parentY;
    private int parentZ;
    private IEquipmentSkinPart skinPart;
    
    public TileEntityBoundingBox() {
    }
    
    public TileEntityBoundingBox(int parentX, int parentY, int parentZ, IEquipmentSkinPart skinPart) {
        setParent(parentX, parentY, parentZ, skinPart);
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
        this.skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(compound.getString(TAG_SKIN_PART));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, this.parentX);
        compound.setInteger(TAG_PARENT_Y, this.parentY);
        compound.setInteger(TAG_PARENT_Z, this.parentZ);
        if (this.skinPart != null) {
            compound.setString(TAG_SKIN_PART, this.skinPart.getRegistryName());
        }
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
    
    public boolean isParentValid() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourerBrain) {
            return true;
        }
        return false;
    }
    
    public IEquipmentSkinPart getSkinPart() {
        return this.skinPart;
    }
    
    public void setParent(int x, int y, int z, IEquipmentSkinPart skinPart) {
        this.parentX = x;
        this.parentY = y;
        this.parentZ = z;
        this.skinPart = skinPart;
        this.markDirty();
    }
}
