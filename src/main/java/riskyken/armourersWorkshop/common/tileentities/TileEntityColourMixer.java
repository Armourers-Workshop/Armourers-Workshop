package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import riskyken.armourersWorkshop.common.items.ItemPaintbrush;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.ModLogger;


public class TileEntityColourMixer extends AbstractTileEntityInventory {
    
    private static final String TAG_COLOUR = "colour";
    public int colour;
    
    public TileEntityColourMixer() {
        items = new ItemStack[2];
        colour = 16777215;
    }
    
    @Override
    public boolean canUpdate() {
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
        if (stackInput != null && stackInput.getItem() == ModItems.paintbrush) {
            ModLogger.log("Painting brush " + colour);
            if (stackOutput != null) { return; }
            setInventorySlotContents(0, null);
            setInventorySlotContents(1, stackInput);
            ((ItemPaintbrush)stackInput.getItem()).setBrushColour(stackInput, colour);
            markDirty();
        }
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.COLOUR_MIXER;
    }

    public void receiveColourUpdateMessage(int colour) {
        this.colour = colour;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        colour = compound.getInteger(TAG_COLOUR);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_COLOUR, colour);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        compound.setInteger(TAG_COLOUR, colour);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 3, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readBaseFromNBT(compound);
        colour = compound.getInteger(TAG_COLOUR);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getColour() {
        return this.colour;
    }
}
