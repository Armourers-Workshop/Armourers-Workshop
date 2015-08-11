package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class TileEntitySkinnable extends TileEntity {

    private static final String TAG_HAS_SKIN = "hasSkin";

    private SkinPointer skinPointer;

    public boolean hasSkin() {
        return skinPointer != null;
    }

    public SkinPointer getSkinPointer() {
        return skinPointer;
    }

    public void setSkinPointer(SkinPointer skinPointer) {
        this.skinPointer = skinPointer;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readFromNBT(compound);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(TAG_HAS_SKIN, hasSkin());
        if (hasSkin()) {
            skinPointer.writeToCompound(compound);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        boolean hasSkin = compound.getBoolean(TAG_HAS_SKIN);
        if (hasSkin) {
            skinPointer = new SkinPointer();
            skinPointer.readFromCompound(compound);
        } else {
            skinPointer = null;
        }
    }
    
    public class SkinnableBlockData {
        
        public final BlockLocation blockLocation;
        public final SkinPointer skinPointer;
        
        public SkinnableBlockData(BlockLocation blockLocation, SkinPointer skinPointer) {
            this.blockLocation = blockLocation;
            this.skinPointer = skinPointer;
        }
    }
}
