package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySkinnable extends TileEntity {

    private static final String TAG_HAS_SKIN = "hasSkin";

    private SkinPointer skinPointer;
    private boolean haveBlockBounds = false;
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;

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

    public void setBoundsOnBlock(Block block) {
        if (haveBlockBounds) {
            block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
            return;
        }
        if (hasSkin()) {
            Skin skin = null;
            if (worldObj.isRemote) {
                skin = getSkinClient(skinPointer);
            } else {
                skin = getSkinServer(skinPointer);
            }
            
            if (skin != null) {
                float scale = 0.0625F;
                Rectangle3D rec = skin.getParts().get(0).getPartBounds();
                minX = rec.getX() * scale;
                minY = rec.getY() * scale;
                minZ = rec.getZ() * scale;
                maxX = (rec.getX() + rec.getWidth()) * scale;
                maxY = (rec.getY() + rec.getHeight()) * scale;
                maxZ = (rec.getZ() + rec.getDepth()) * scale;
                rotateBlockBounds();
                haveBlockBounds = true;
                block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                return;
            }
        }
        block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
    }

    private void rotateBlockBounds() {
        ForgeDirection dir = ForgeDirection.UNKNOWN;
        int meta = getBlockMetadata() % 4;
        
        float oldMinX = minX;
        float oldMinZ = minZ;
        float oldMaxX = maxX;
        float oldMaxZ = maxZ;
        
        if (meta == 0) {
            dir = ForgeDirection.SOUTH;
        }
        if (meta == 1) {
            dir = ForgeDirection.WEST;
        }
        if (meta == 2) {
            dir = ForgeDirection.NORTH;
        }
        if (meta == 3) {
            dir = ForgeDirection.EAST;
        }
        
        switch (dir) {
        case SOUTH:
            minZ = 1 - oldMaxZ;
            maxZ = 1 - oldMinZ;
            minX = 1 - oldMaxX;
            maxX = 1 - oldMinX;
            break;
        case EAST:
            maxX = 1 - oldMinZ;
            minX = 1 - oldMaxZ;
            maxZ = oldMaxX;
            minZ = oldMinX;
            break;
        case WEST:
            maxX = oldMaxZ;
            minX = oldMinZ;
            maxZ = 1 - oldMinX;
            minZ = 1 - oldMaxX;
            break; 
        default:
            break;
        }
    }

    @SideOnly(Side.CLIENT)
    private Skin getSkinClient(ISkinPointer skinPointer) {
        return ClientModelCache.INSTANCE.getEquipmentItemData(skinPointer.getSkinId());
    }

    private Skin getSkinServer(ISkinPointer skinPointer) {
        return SkinDataCache.INSTANCE.softGetSkin(skinPointer.getSkinId());
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

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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
