package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import riskyken.armourersWorkshop.common.items.IColourTool;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ItemPaintRoller;
import riskyken.armourersWorkshop.common.items.ItemPaintbrush;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityColourMixer extends AbstractTileEntityInventory implements IColourable {
    
    private static final String TAG_ITEM_UPDATE = "itemUpdate";
    
    public int colour;
    
    private boolean itemUpdate;
    private boolean colourUpdate;
    
    public TileEntityColourMixer() {
        items = new ItemStack[2];
        colour = 16777215;
        colourUpdate = false;
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
        
        if (stackInput != null && stackInput.getItem() instanceof IColourTool) {
            if (stackOutput != null) { return; }
            setInventorySlotContents(0, null);
            setInventorySlotContents(1, stackInput);
            
            if (stackInput.getItem() == ModItems.paintbrush) {
                ((ItemPaintbrush)stackInput.getItem()).setToolColour(stackInput, colour);
            }
            if (stackInput.getItem() == ModItems.paintRoller) {
                ((ItemPaintRoller)stackInput.getItem()).setToolColour(stackInput, colour);
            }
            if (stackInput.getItem() == ModItems.colourPicker) {
                setColour(((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput), true);
            }
            markDirty();
        }
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.COLOUR_MIXER;
    }

    public void receiveColourUpdateMessage(int colour, boolean item) {
        setColour(colour, item);
    }
    
    public void setColour(int colour, boolean item){
        if (worldObj.isRemote) { return; }
        if (item) { itemUpdate = true; }
        this.colour = colour;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setBoolean(TAG_ITEM_UPDATE, itemUpdate);
        if (itemUpdate) { itemUpdate = false; }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 3, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readBaseFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
        itemUpdate = compound.getBoolean(TAG_ITEM_UPDATE);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

    public int getColour() {
        return this.colour;
    }

    @Override
    public void setColour(int colour) {
        setColour(colour, false);
    }
}
