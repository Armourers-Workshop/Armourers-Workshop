package moe.plushie.armourers_workshop.common.tileentities;

import java.awt.Color;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.client.gui.colour_mixer.GuiColourMixer;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemColourPicker;
import moe.plushie.armourers_workshop.common.inventory.ContainerColourMixer;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibCommonTags;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityColourMixer extends AbstractTileEntityInventory implements IPantable, IGuiFactory {

    private static final String TAG_ITEM_UPDATE = "itemUpdate";
    private static final String TAG_PAINT_TYPE = "paintType";
    private static final int INVENTORY_SIZE = 2;

    public int colour;
    private IPaintType paintType;

    private boolean itemUpdate;
    private boolean colourUpdate;

    public TileEntityColourMixer() {
        super(INVENTORY_SIZE);
        colour = 16777215;
        paintType = PaintTypeRegistry.PAINT_TYPE_NORMAL;
        colourUpdate = false;
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

            if (stackInput.getItem() instanceof IPaintingTool && stackInput.getItem() != ModItems.COLOUR_PICKER) {
                IPaintingTool paintingTool = (IPaintingTool) stackInput.getItem();
                paintingTool.setToolColour(stackInput, colour);
                paintingTool.setToolPaintType(stackInput, getPaintType(0));
            }
            if (stackInput.getItem() == ModItems.COLOUR_PICKER) {
                setPaintType(((ItemColourPicker) stackInput.getItem()).getToolPaintType(stackInput), 0);
                setColour(((ItemColourPicker) stackInput.getItem()).getToolColour(stackInput), true);
            }
            markDirty();
        }
    }

    @Override
    public String getName() {
        return LibBlockNames.COLOUR_MIXER;
    }

    public void receiveColourUpdateMessage(int colour, boolean item, IPaintType paintType) {
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
        if (compound.hasKey(TAG_PAINT_TYPE)) {
            paintType = PaintTypeRegistry.getInstance().getPaintTypeFromIndex(compound.getInteger(TAG_PAINT_TYPE));
        } else {
            paintType = PaintTypeRegistry.PAINT_TYPE_NORMAL;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_PAINT_TYPE, paintType.getId());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_PAINT_TYPE, paintType.getId());
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
        paintType = PaintTypeRegistry.getInstance().getPaintTypeFromIndex(compound.getInteger(TAG_PAINT_TYPE));
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
    public void setPaintType(IPaintType paintType, int side) {
        this.paintType = paintType;
        dirtySync();
    }

    @Override
    public IPaintType getPaintType(int side) {
        return paintType;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerColourMixer(player.inventory, this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiColourMixer(player.inventory, this);
    }
}
