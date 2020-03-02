package moe.plushie.armourers_workshop.common.tileentities;

import java.awt.Point;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.SkinTextureHelper;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBoundingBox extends ModTileEntity {
    
    private static final String TAG_PARENT = "parent";
    private static final String TAG_GUIDE_X = "guideX";
    private static final String TAG_GUIDE_Y = "guideY";
    private static final String TAG_GUIDE_Z = "guideZ";
    private static final String TAG_SKIN_PART = "skinPart";
    
    private BlockPos parent;
    private byte guideX;
    private byte guideY;
    private byte guideZ;
    private ISkinPartType skinPart;
    
    private static final AxisAlignedBB blockBounds = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    
    public TileEntityBoundingBox() {
        setParent(null, (byte) 0, (byte) 0, (byte) 0, null);
    }
    
    public TileEntityBoundingBox(BlockPos parent, byte guideX, byte guideY, byte guideZ, ISkinPartType skinPart) {
        setParent(parent, guideX, guideY, guideZ, skinPart);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.parent = NBTHelper.readBlockPosFromNBT(compound, TAG_PARENT);
        this.guideX = compound.getByte(TAG_GUIDE_X);
        this.guideY = compound.getByte(TAG_GUIDE_Y);
        this.guideZ = compound.getByte(TAG_GUIDE_Z);
        this.skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(compound.getString(TAG_SKIN_PART));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTHelper.writeBlockPosToNBT(compound, TAG_PARENT, parent);
        compound.setByte(TAG_GUIDE_X, this.guideX);
        compound.setByte(TAG_GUIDE_Y, this.guideY);
        compound.setByte(TAG_GUIDE_Z, this.guideZ);
        if (this.skinPart != null) {
            compound.setString(TAG_SKIN_PART, this.skinPart.getRegistryName());
        }
        return compound;
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        syncWithClients();
    }
    
    public TileEntityArmourer getParent() {
        if (parent != null) {
            TileEntity te = getWorld().getTileEntity(parent);
            if (te != null && te instanceof TileEntityArmourer) {
                return (TileEntityArmourer)te;
            }
        }
        return null;
    }
    
    public boolean isParentValid() {
        if (parent != null) {
            TileEntity te = getWorld().getTileEntity(parent);
            if (te != null && te instanceof TileEntityArmourer) {
                return true;
            }
        }
        return false;
    }
    
    public ISkinPartType getSkinPart() {
        return this.skinPart;
    }
    
    public void setParent(BlockPos parent, byte guideX, byte guideY, byte guideZ, ISkinPartType skinPart) {
        this.parent = parent;
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
    
    public boolean isPaintableSide(int side) {
        EnumFacing sideBlock = EnumFacing.byIndex(side);
        if (getWorld().getBlockState(getPos().offset(sideBlock)).getBlock() == getBlockType()) {
            return false;
        }
        return true;
    }
    
    public IPaintType getPaintType(EnumFacing facing) {
        if (isParentValid() && skinPart instanceof ISkinPartTypeTextured) {
            
            Point texPoint = SkinTextureHelper.getTextureLocationFromWorldBlock(this, facing);
            int colour = getParent().getPaintData(texPoint.x, texPoint.y);
            return PaintTypeRegistry.getInstance().getPaintTypeFromColour(colour);
        } else {
            //ModLogger.log("x" + parentX);
            return PaintTypeRegistry.PAINT_TYPE_NORMAL;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return blockBounds.offset(getPos());
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return ConfigHandlerClient.renderDistanceBlockSkin;
    }
}
