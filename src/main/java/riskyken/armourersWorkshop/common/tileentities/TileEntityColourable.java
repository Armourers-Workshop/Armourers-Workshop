package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;

public class TileEntityColourable extends TileEntity implements IPantable {
    
    private ICubeColour colour;

    public TileEntityColourable() {
        this.colour = new CubeColour();
    }
    
    public TileEntityColourable(int colour) {
        this.colour = new CubeColour(colour);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }

    @SuppressWarnings("deprecation")
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
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        colour.writeToNBT(compound);
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

    @SuppressWarnings("deprecation")
    @Override
    public void setColour(int colour) {
        this.colour.setColour(colour);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void setColour(int colour, int side) {
        this.colour.setColour(colour, side);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void setColour(byte[] rgb, int side) {
        this.colour.setRed(rgb[0], side);
        this.colour.setGreen(rgb[1], side);
        this.colour.setBlue(rgb[2], side);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        this.colour = colour;
    }

    @Override
    public int getColour(int side) {
        Color saveColour = new Color(colour.getRed(side) & 0xFF, colour.getGreen(side) & 0xFF, colour.getBlue(side) & 0xFF);
        return saveColour.getRGB();
    }
    
    @Override
    public void setPaintType(PaintType paintType, int side) {
        colour.setPaintType((byte)paintType.getKey(), side);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public PaintType getPaintType(int side) {
        return PaintType.getPaintTypeFormSKey(colour.getPaintType(side));
    }
    
    @Override
    public ICubeColour getColour() {
        return colour;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 2048;
    }
}
