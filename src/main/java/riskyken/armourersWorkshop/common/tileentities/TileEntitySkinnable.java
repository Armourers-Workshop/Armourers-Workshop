package riskyken.armourersWorkshop.common.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;

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
    
    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        haveBlockBounds = false;
        
    }

    public void setBoundsOnBlock(Block block, int xOffset, int yOffset, int zOffset) {
        if (haveBlockBounds) {
            //block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
            //return;
        }
        if (hasSkin()) {
            Skin skin = null;
            if (worldObj.isRemote) {
                skin = getSkinClient(skinPointer);
            } else {
                skin = getSkinServer(skinPointer);
            }
            
            if (skin != null) {
                ForgeDirection dir = getDirectionFromMeta(getBlockMetadata());
                float[] bounds = getBlockBounds(skin, 1 - dir.offsetX, 0, 1 + dir.offsetZ, dir);
                if (bounds != null) {
                    minX = bounds[0];
                    minY = bounds[1];
                    minZ = bounds[2];
                    maxX = bounds[3];
                    maxY = bounds[4];
                    maxZ = bounds[5];
                    haveBlockBounds = true;
                    block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                }
                return;
            }
        }
        if (haveBlockBounds) {
            block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }
    
    public static ForgeDirection getDirectionFromMeta(int meta) {
        ForgeDirection dir = ForgeDirection.UNKNOWN;
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
        return dir;
    }
    
    public static float[] getBlockBounds(Skin skin, int gridX, int gridY, int gridZ, ForgeDirection dir) {
        float[] bounds = new float[6];
        float scale = 0.0625F;
        SkinPart skinPart = skin.getParts().get(0);
        
        gridX = MathHelper.clamp_int(gridX, 0, 2);
        gridY = MathHelper.clamp_int(gridY, 0, 2);
        gridZ = MathHelper.clamp_int(gridZ, 0, 2);
        
        Rectangle3D rec = skinPart.getBlockBounds(gridX, gridY, gridZ);
        switch (dir) {
        case SOUTH:
            rec = skinPart.getBlockBounds(2 - gridX, gridY, 2 - gridZ);
            break;
        case EAST:
            rec = skinPart.getBlockBounds(2 - gridZ, gridY, gridX);
            break;
        case WEST:
            rec = skinPart.getBlockBounds(gridZ, gridY, 2 - gridX);
            break;
        default:
            break;
        }
        
        if (rec != null) {
            int x = 8 + rec.getX();
            int y = 8 - rec.getHeight() - rec.getY();
            int z = 8 - rec.getDepth() - rec.getZ();
            bounds[0] = x * scale;
            bounds[1] = y * scale;
            bounds[2] = z * scale;
            bounds[3] = (x + rec.getWidth()) * scale;
            bounds[4] = (y + rec.getHeight()) * scale;
            bounds[5] = (z + rec.getDepth()) * scale;
            bounds = rotateBlockBounds(bounds, dir);
        } else {
            return null;
        }
        
        return bounds;
    }
    
    private static float[] rotateBlockBounds(float[] bounds, ForgeDirection dir) {
        float[] rotatedBounds = new float[6];
        for (int i = 0; i < bounds.length; i++) {
            rotatedBounds[i] = bounds[i];
        }
        switch (dir) {
        case SOUTH:
            rotatedBounds[0] = 1 - bounds[3]; //oldMaxX - minX
            rotatedBounds[2] = 1 - bounds[5]; //oldMaxZ - minZ
            rotatedBounds[3] = 1 - bounds[0]; //oldMinX - maxX
            rotatedBounds[5] = 1 - bounds[2]; //oldMinZ - maxZ
            break;
        case EAST:
            rotatedBounds[0] = 1 - bounds[5]; //oldMaxZ - minX
            rotatedBounds[2] = bounds[0];     //oldMinX - minZ
            rotatedBounds[3] = 1 - bounds[2]; //oldMinZ - maxX
            rotatedBounds[5] = bounds[3];     //oldMaxX - maxZ
            break;
        case WEST:
            rotatedBounds[0] = bounds[2];     //oldMinZ - minX
            rotatedBounds[2] = 1 - bounds[3]; //oldMaxX - minZ
            rotatedBounds[3] = bounds[5];     //oldMaxZ - maxX
            rotatedBounds[5] = 1 - bounds[0]; //oldMinX - maxZ
            break; 
        default:
            break;
        }
        return rotatedBounds;
    }

    @SideOnly(Side.CLIENT)
    private Skin getSkinClient(ISkinPointer skinPointer) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }

    private Skin getSkinServer(ISkinPointer skinPointer) {
        return CommonSkinCache.INSTANCE.softGetSkin(skinPointer.getSkinId());
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
    
    @Override
    public double getMaxRenderDistanceSquared() {
        return ConfigHandlerClient.blockSkinMaxRenderDistance;
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
