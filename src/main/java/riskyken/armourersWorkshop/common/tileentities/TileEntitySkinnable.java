package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class TileEntitySkinnable extends TileEntity {

    public static final ArrayList<SkinnableBlockData> BLOCK_UPDATE_LIST = new ArrayList<SkinnableBlockData>();
    private static final String TAG_HAS_SKIN = "hasSkin";
    
    public static void updateBlocksWithSkin(SkinPointer skinPointer, World world) {
        for (int i = 0; i < BLOCK_UPDATE_LIST.size(); i++) {
            SkinnableBlockData sbd = BLOCK_UPDATE_LIST.get(i);
            if (sbd.skinPointer.skinId == skinPointer.skinId) {
                world.markBlockForUpdate(sbd.blockLocation.x, sbd.blockLocation.y, sbd.blockLocation.z);
                BLOCK_UPDATE_LIST.remove(i);
            }
        }
    }

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
        if (worldObj.isRemote) {
            addToRenderUpdateList();
        } else {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
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
        addToRenderUpdateList();
    }

    private void addToRenderUpdateList() {
        if (hasSkin()) {
            if (EquipmentRenderHandler.INSTANCE.isSkinInModelCache(skinPointer)) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else {
                BLOCK_UPDATE_LIST.add(new SkinnableBlockData(new BlockLocation(xCoord, yCoord, zCoord), skinPointer));
            }
        } else {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
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
