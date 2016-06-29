package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

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
        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }
    
    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        haveBlockBounds = false;
        
    }

    public AxisAlignedBB getAABBForBlock(IBlockAccess world, IBlockState state) {
        return state.getBlock().getBoundingBox(state, world, getPos());
        /*
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
                SkinPart skinPart = skin.getParts().get(0);
                Rectangle3D rec = skinPart.getPartBounds();
                IRectangle3D buildSpace = skinPart.getPartType().getBuildingSpace();
                
                int x = buildSpace.getX() + buildSpace.getWidth() + rec.getX();
                int y = buildSpace.getY() + buildSpace.getHeight() - rec.getY() - rec.getHeight();
                int z = buildSpace.getZ() + buildSpace.getDepth() - rec.getZ() - rec.getDepth();
                minX = x * scale;
                minY = y * scale;
                minZ = z * scale;
                maxX = (x + rec.getWidth()) * scale;
                maxY = (y + rec.getHeight()) * scale;
                maxZ = (z + rec.getDepth()) * scale;
                rotateBlockBounds();
                haveBlockBounds = true;
                block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                return;
            }
        }
        block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        */
    }

    private void rotateBlockBounds() {
        EnumFacing dir = null;
        int meta = getBlockMetadata() % 4;
        
        float oldMinX = minX;
        float oldMinZ = minZ;
        float oldMaxX = maxX;
        float oldMaxZ = maxZ;
        
        if (meta == 0) {
            dir = EnumFacing.SOUTH;
        }
        if (meta == 1) {
            dir = EnumFacing.WEST;
        }
        if (meta == 2) {
            dir = EnumFacing.NORTH;
        }
        if (meta == 3) {
            dir = EnumFacing.EAST;
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
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }

    private Skin getSkinServer(ISkinPointer skinPointer) {
        return SkinDataCache.INSTANCE.softGetSkin(skinPointer.getSkinId());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), compound);
    }


    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound compound = pkt.getNbtCompound();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(TAG_HAS_SKIN, hasSkin());
        if (hasSkin()) {
            skinPointer.writeToCompound(compound);
        }
        return compound;
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
        return new AxisAlignedBB(getPos());
    }
    
    @Override
    public double getMaxRenderDistanceSquared() {
        return ConfigHandler.blockSkinMaxRenderDistance;
    }

    public class SkinnableBlockData {
        
        public final BlockPos blockPos;
        public final SkinPointer skinPointer;
        
        public SkinnableBlockData(BlockPos blockPos, SkinPointer skinPointer) {
            this.blockPos = blockPos;
            this.skinPointer = skinPointer;
        }
    }
}
