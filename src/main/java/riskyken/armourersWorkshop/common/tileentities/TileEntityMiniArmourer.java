package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiniArmourer extends AbstractTileEntityInventory {

    private static final String TAG_TYPE = "type";
    
    @SideOnly(Side.CLIENT)
    public int red;
    @SideOnly(Side.CLIENT)
    public int green;
    @SideOnly(Side.CLIENT)
    public int blue;
    
    private EnumEquipmentType equipmentType = EnumEquipmentType.HEAD;
    
    public TileEntityMiniArmourer() {
        this.items = new ItemStack[2];
    }
    
    public EnumEquipmentType getEquipmentType() {
        return equipmentType;
    }
    
    public void setEquipmentType(EnumEquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }
    
    public void nextEquipmentType() {
        int value = equipmentType.ordinal();
        value++;
        if (value > equipmentType.values().length - 2) {
            value = 1;
        }
        setEquipmentType(EnumEquipmentType.getOrdinal(value));
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        equipmentType = EnumEquipmentType.getOrdinal(compound.getInteger(TAG_TYPE));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_TYPE, equipmentType.ordinal());
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.MINI_ARMOURER;
    }
}
