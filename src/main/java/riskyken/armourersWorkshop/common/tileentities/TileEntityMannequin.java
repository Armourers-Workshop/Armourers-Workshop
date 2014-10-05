package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.customEquipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.customEquipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.customEquipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityMannequin extends AbstractTileEntityInventory {

    private EntityEquipmentData equipmentData;
    
    public TileEntityMannequin() {
        this.items = new ItemStack[5];
        equipmentData = new EntityEquipmentData();
    }
    
    public void setEquipment(ItemStack stack) {
        if (!stack.hasTagCompound()) { return; }
        NBTTagCompound data = stack.getTagCompound();
        if (!data.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return ;}
        NBTTagCompound armourNBT = data.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
        CustomArmourItemData equipmentData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        setEquipment(equipmentData.getType(), equipmentId);
    }
    
    public void setEquipment(EnumArmourType armourType, int equipmentId) {
        equipmentData.addEquipment(armourType, equipmentId);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        equipmentData.loadNBTData(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        equipmentData.saveNBTData(compound);
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
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.MANNEQUIN;
    }
    
    public EntityEquipmentData getEquipmentData() {
        return equipmentData;
    }
}
