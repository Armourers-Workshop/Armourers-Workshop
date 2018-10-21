package moe.plushie.armourers_workshop.common.tileentities;

import java.awt.Color;

import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemColourPicker;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibCommonTags;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import moe.plushie.armourers_workshop.utils.UtilColour.ColourFamily;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityColourMixer extends AbstractTileEntityInventory implements IPantable {

    private static final String TAG_ITEM_UPDATE = "itemUpdate";
    private static final String TAG_COLOUR_FAMILY = "colourFamily";
    private static final String TAG_PAINT_TYPE = "paintType";
    private static final int INVENTORY_SIZE = 2;

    public int colour;
    private PaintType paintType;
    private ColourFamily colourFamily;

    private boolean itemUpdate;
    private boolean colourUpdate;

    public TileEntityColourMixer() {
        super(INVENTORY_SIZE);
        colour = 16777215;
        paintType = PaintType.NORMAL;
        colourUpdate = false;
        colourFamily = ColourFamily.MINECRAFT;
    }

    public boolean isSpecial() {
        int meta = getBlockMetadata();
        if (meta == 1) {
            return true;
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        checkForPaintBrush();
    }

    private void checkForPaintBrush() {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);

        if (stackInput.getItem() instanceof IPaintingTool) {
            if (!stackOutput.isEmpty()) {
                return;
            }
            setInventorySlotContents(0, ItemStack.EMPTY);
            setInventorySlotContents(1, stackInput);

            if (stackInput.getItem() instanceof IPaintingTool && stackInput.getItem() != ModItems.colourPicker) {
                IPaintingTool paintingTool = (IPaintingTool) stackInput.getItem();
                paintingTool.setToolColour(stackInput, colour);
                paintingTool.setToolPaintType(stackInput, getPaintType(0));
            }
            if (stackInput.getItem() == ModItems.colourPicker) {
                setPaintType(((ItemColourPicker) stackInput.getItem()).getToolPaintType(stackInput), 0);
                setColour(((ItemColourPicker) stackInput.getItem()).getToolColour(stackInput), true);
            }
            markDirty();
        }
    }

    public void setColourFamily(ColourFamily colourFamily) {
        this.colourFamily = colourFamily;
        markDirty();
    }

    public ColourFamily getColourFamily() {
        return colourFamily;
    }

    @Override
    public String getName() {
        return LibBlockNames.COLOUR_MIXER;
    }

    public void receiveColourUpdateMessage(int colour, boolean item, PaintType paintType) {
        setColour(colour, item);
        setPaintType(paintType, 0);
    }

    public void setColour(int colour, boolean item) {
        if (item) {
            itemUpdate = true;
        }
        this.colour = colour;
        dirtySync();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
        colourFamily = ColourFamily.values()[compound.getInteger(TAG_COLOUR_FAMILY)];
        if (compound.hasKey(TAG_PAINT_TYPE)) {
            paintType = PaintType.getPaintTypeFromUKey(compound.getInteger(TAG_PAINT_TYPE));
        } else {
            paintType = PaintType.NORMAL;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_COLOUR_FAMILY, colourFamily.ordinal());
        compound.setInteger(TAG_PAINT_TYPE, paintType.getKey());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_PAINT_TYPE, paintType.getKey());
        compound.setBoolean(TAG_ITEM_UPDATE, itemUpdate);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        if (itemUpdate) {
            itemUpdate = false;
        }
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        readBaseFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
        paintType = PaintType.getPaintTypeFromUKey(compound.getInteger(TAG_PAINT_TYPE));
        itemUpdate = compound.getBoolean(TAG_ITEM_UPDATE);
        syncWithClients();
        colourUpdate = true;
    }

    @SideOnly(Side.CLIENT)
    public boolean getHasItemUpdateAndReset() {
        if (itemUpdate) {
            itemUpdate = false;
            return true;
        }
        return false;
    }

    @Override
    public int getColour(int side) {
        return this.colour;
    }

    @Override
    public ICubeColour getColour() {
        return new CubeColour(colour);
    }

    @Deprecated
    @Override
    public void setColour(int colour) {
        setColour(colour, false);
    }

    @Override
    public void setColour(byte[] rgb, int side) {
        setColour(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF).getRGB(), false);
    }

    @Deprecated
    @Override
    public void setColour(int colour, int side) {
        setColour(colour, false);
    }

    @Override
    public void setColour(ICubeColour colour) {
        // NO-OP
        // setColour(colour.g);
    }

    @Override
    public void setPaintType(PaintType paintType, int side) {
        this.paintType = paintType;
        dirtySync();
    }

    @Override
    public PaintType getPaintType(int side) {
        return paintType;
    }
}
