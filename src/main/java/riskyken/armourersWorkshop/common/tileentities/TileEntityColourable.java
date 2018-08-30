package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        colour.writeToNBT(compound);
        return compound;
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

    @SuppressWarnings("deprecation")
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
        Color saveColour = new Color(colour.getRed(side) & 0xFF, colour.getGreen(side) & 0xFF, colour.getBlue(side) & 0xFF);
        return saveColour.getRGB();
    }
    
    @Override
    public void setPaintType(PaintType paintType, int side) {
        colour.setPaintType((byte)paintType.getKey(), side);
        dirtySync();
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
        return new AxisAlignedBB(getPos());
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 2048;
    }
}
