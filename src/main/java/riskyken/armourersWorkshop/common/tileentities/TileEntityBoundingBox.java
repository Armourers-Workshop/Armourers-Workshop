package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Point;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.SkinTextureHelper;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.NBTHelper;

public class TileEntityBoundingBox extends TileEntity {
    
    private static final String TAG_PARENT_POS = "parent";
    private static final String TAG_GUIDE_X = "guideX";
    private static final String TAG_GUIDE_Y = "guideY";
    private static final String TAG_GUIDE_Z = "guideZ";
    private static final String TAG_SKIN_PART = "skinPart";
    
    private BlockPos parentPos;
    private byte guideX;
    private byte guideY;
    private byte guideZ;
    private ISkinPartType skinPart;
    
    public TileEntityBoundingBox() {
        setParent(new BlockPos(0, 0, 0), (byte) 0, (byte) 0, (byte) 0, null);
    }
    
    public TileEntityBoundingBox(BlockPos parentPos,
            byte guideX, byte guideY, byte guideZ, ISkinPartType skinPart) {
        setParent(parentPos, guideX, guideY, guideZ, skinPart);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.parentPos = NBTHelper.readBlockPos(compound, TAG_PARENT_POS);
        this.guideX = compound.getByte(TAG_GUIDE_X);
        this.guideY = compound.getByte(TAG_GUIDE_Y);
        this.guideZ = compound.getByte(TAG_GUIDE_Z);
        this.skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(compound.getString(TAG_SKIN_PART));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTHelper.writeBlockPos(parentPos, compound, TAG_PARENT_POS);
        compound.setByte(TAG_GUIDE_X, this.guideX);
        compound.setByte(TAG_GUIDE_Y, this.guideY);
        compound.setByte(TAG_GUIDE_Z, this.guideZ);
        if (this.skinPart != null) {
            compound.setString(TAG_SKIN_PART, this.skinPart.getRegistryName());
        }
        return compound;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    public TileEntityArmourer getParent() {
        TileEntity te = worldObj.getTileEntity(parentPos);
        if (te != null && te instanceof TileEntityArmourer) {
            return (TileEntityArmourer)te;
        }
        return null;
    }
    
    public boolean isParentValid() {
        TileEntity te = worldObj.getTileEntity(parentPos);
        if (te != null && te instanceof TileEntityArmourer) {
            return true;
        }
        return false;
    }
    
    public ISkinPartType getSkinPart() {
        return this.skinPart;
    }
    
    public void setParent(BlockPos parentPos, byte guideX, byte guideY, byte guideZ,
            ISkinPartType skinPart) {
        this.parentPos = parentPos;
        this.guideX = guideX;
        this.guideY = guideY;
        this.guideZ = guideZ;
        this.skinPart = skinPart;
        this.markDirty();
    }
    
    public byte getGuideX() {
        return this.guideX;
    }
    
    public byte getGuideY() {
        return this.guideY;
    }
    
    public byte getGuideZ() {
        return this.guideZ;
    }
    
    public boolean isPaintableSide(EnumFacing side) {
        if (worldObj.getBlockState(getPos().offset(side)).getBlock() == getBlockType()) {
            return false;
        }
        return true;
    }
    
    public PaintType getPaintType(EnumFacing side) {
        if (isParentValid() && skinPart instanceof ISkinPartTypeTextured) {
            Point texPoint = SkinTextureHelper.getTextureLocationFromWorldBlock(this, side);
            int colour = getParent().getPaintData(texPoint.x, texPoint.y);
            return PaintType.getPaintTypeFromColour(colour);
        } else {
            //ModLogger.log("x" + parentX);
            return PaintType.DYE_1;
        }
    }
}
