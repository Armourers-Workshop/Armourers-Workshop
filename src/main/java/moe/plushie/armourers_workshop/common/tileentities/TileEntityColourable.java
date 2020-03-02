package moe.plushie.armourers_workshop.common.tileentities;

import java.awt.Color;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        colour.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        colour.writeToNBT(compound);
        return compound;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 5, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        syncWithClients();
    }

    @Override
    public void setColour(int colour) {
        this.colour.setColour(colour);
        dirtySync();
    }
    
    @Override
    public void setColour(int colour, int side) {
        this.colour.setColour(colour, side);
        dirtySync();
    }
    
    @Override
    public void setColour(byte[] rgb, int side) {
        this.colour.setRed(rgb[0], side);
        this.colour.setGreen(rgb[1], side);
        this.colour.setBlue(rgb[2], side);
        dirtySync();
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        this.colour = colour;
    }

    @Override
    public int getColour(int side) {
        Color saveColour = new Color(colour.getRed(side) & 0xFF, colour.getGreen(side) & 0xFF, colour.getBlue(side) & 0xFF, 255);
        return saveColour.getRGB();
    }
    
    @Override
    public void setPaintType(IPaintType paintType, int side) {
        colour.setPaintType((byte)paintType.getId(), side);
        dirtySync();
    }
    
    @Override
    public IPaintType getPaintType(int side) {
        return PaintTypeRegistry.getInstance().getPaintTypeFormByte(colour.getPaintType(side));
    }
    
    @Override
    public ICubeColour getColour() {
        return colour;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos());
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 2048;
    }
}
