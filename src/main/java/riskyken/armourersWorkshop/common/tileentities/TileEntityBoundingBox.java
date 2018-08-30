package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Point;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.SkinTextureHelper;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class TileEntityBoundingBox extends TileEntity {
    
    private static final String TAG_PARENT_X = "parentX";
    private static final String TAG_PARENT_Y = "parentY";
    private static final String TAG_PARENT_Z = "parentZ";
    private static final String TAG_GUIDE_X = "guideX";
    private static final String TAG_GUIDE_Y = "guideY";
    private static final String TAG_GUIDE_Z = "guideZ";
    private static final String TAG_SKIN_PART = "skinPart";
    
    private BlockPos parent;
    private byte guideX;
    private byte guideY;
    private byte guideZ;
    private ISkinPartType skinPart;
    
    
    public TileEntityBoundingBox() {
        setParent(0, 0, 0, (byte) 0, (byte) 0, (byte) 0, null);
    }
    
    public TileEntityBoundingBox(BlockPos parent, byte guideX, byte guideY, byte guideZ, ISkinPartType skinPart) {
        setParent(parent, guideX, guideY, guideZ, skinPart);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.parentX = compound.getInteger(TAG_PARENT_X);
        this.parentY = compound.getInteger(TAG_PARENT_Y);
        this.parentZ = compound.getInteger(TAG_PARENT_Z);
        this.guideX = compound.getByte(TAG_GUIDE_X);
        this.guideY = compound.getByte(TAG_GUIDE_Y);
        this.guideZ = compound.getByte(TAG_GUIDE_Z);
        this.skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(compound.getString(TAG_SKIN_PART));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_PARENT_X, this.parentX);
        compound.setInteger(TAG_PARENT_Y, this.parentY);
        compound.setInteger(TAG_PARENT_Z, this.parentZ);
        compound.setByte(TAG_GUIDE_X, this.guideX);
        compound.setByte(TAG_GUIDE_Y, this.guideY);
        compound.setByte(TAG_GUIDE_Z, this.guideZ);
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
    
    public TileEntityArmourer getParent() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourer) {
            return (TileEntityArmourer)te;
        }
        return null;
    }
    
    public boolean isParentValid() {
        TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
        if (te != null && te instanceof TileEntityArmourer) {
            return true;
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
        ForgeDirection sideBlock = ForgeDirection.getOrientation(side);
        if (worldObj.getBlock(xCoord + sideBlock.offsetX, yCoord + sideBlock.offsetY, zCoord + sideBlock.offsetZ) == getBlockType()) {
            return false;
        }
        return true;
    }
    
    public PaintType getPaintType(int side) {
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
