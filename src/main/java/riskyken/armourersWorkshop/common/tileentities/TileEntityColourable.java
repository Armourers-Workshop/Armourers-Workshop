package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;

public class TileEntityColourable extends ModTileEntity implements IPantable {
    
    private ICubeColour colour;

    public TileEntityColourable() {
        this.colour = new CubeColour();
    }
    
    public TileEntityColourable(int colour) {
        this.colour = new CubeColour(colour);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(LibCommonTags.TAG_COLOUR)) {
            colour.setColour(compound.getInteger(LibCommonTags.TAG_COLOUR));
        } else {
            colour.readFromNBT(compound);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        colour.writeToNBT(compound);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    @Override
    public void setColour(int colour) {
        this.colour.setColour(colour);
        markDirty();
        syncWithClients();
    }
    
    @Override
    public void setColour(int colour, EnumFacing side) {
        this.colour.setColour(colour, side);
        markDirty();
        syncWithClients();
    }
    
    @Override
    public void setColour(byte[] rgb, EnumFacing side) {
        this.colour.setRed(rgb[0], side);
        this.colour.setGreen(rgb[1], side);
        this.colour.setBlue(rgb[2], side);
        markDirty();
        syncWithClients();
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        this.colour = colour;
    }

    @Override
    public int getColour(EnumFacing side) {
        Color saveColour = new Color(colour.getRed(side) & 0xFF, colour.getGreen(side) & 0xFF, colour.getBlue(side) & 0xFF);
        return saveColour.getRGB();
    }
    
    @Override
    public void setPaintType(PaintType paintType, EnumFacing side) {
        colour.setPaintType((byte)paintType.getKey(), side);
        markDirty();
        syncWithClients();
    }
    
    @Override
    public PaintType getPaintType(EnumFacing side) {
        return PaintType.getPaintTypeFormSKey(colour.getPaintType(side));
    }
    
    @Override
    public ICubeColour getColour() {
        return colour;
    }
}
