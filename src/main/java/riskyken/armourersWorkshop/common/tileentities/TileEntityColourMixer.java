package riskyken.armourersWorkshop.common.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.paintingtool.ItemColourPicker;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;


public class TileEntityColourMixer extends AbstractTileEntityInventory implements IPantable {
    
    private static final String TAG_ITEM_UPDATE = "itemUpdate";
    private static final String TAG_COLOUR_FAMILY = "colourFamily";
    private static final String TAG_PAINT_TYPE = "paintType";
    
    public int colour;
    private PaintType paintType;
    private ColourFamily colourFamily;
    
    private boolean itemUpdate;
    private boolean colourUpdate;
    
    public TileEntityColourMixer() {
        items = new ItemStack[2];
        colour = 16777215;
        paintType = PaintType.NORMAL;
        colourUpdate = false;
        colourFamily = ColourFamily.MINECRAFT_WOOL;
    }
    
    public boolean isSpecial() {
        int meta = getBlockMetadata();
        if (meta == 1) {
            return true;
        }
        return false;
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
        
        if (stackInput != null && stackInput.getItem() instanceof IPaintingTool) {
            if (stackOutput != null) { return; }
            setInventorySlotContents(0, null);
            setInventorySlotContents(1, stackInput);
            
            if (stackInput.getItem() instanceof IPaintingTool && stackInput.getItem() != ModItems.colourPicker) {
                IPaintingTool paintingTool = (IPaintingTool) stackInput.getItem();
                paintingTool.setToolColour(stackInput, colour);
                paintingTool.setToolPaintType(stackInput, getPaintType(0));
            }
            if (stackInput.getItem() == ModItems.colourPicker) {
                setPaintType(((ItemColourPicker)stackInput.getItem()).getToolPaintType(stackInput), 0);
                setColour(((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput), true);
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
    public String getInventoryName() {
        return LibBlockNames.COLOUR_MIXER;
    }

    public void receiveColourUpdateMessage(int colour, boolean item, PaintType paintType) {
        setColour(colour, item);
        setPaintType(paintType, 0);
    }
    
    public void setColour(int colour, boolean item){
        if (worldObj.isRemote) {
            return;
        }
        if (item) {
            itemUpdate = true;
        }
        this.colour = colour;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_COLOUR_FAMILY, colourFamily.ordinal());
        compound.setInteger(TAG_PAINT_TYPE, paintType.getKey());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        compound.setInteger(TAG_PAINT_TYPE, paintType.getKey());
        compound.setBoolean(TAG_ITEM_UPDATE, itemUpdate);
        if (itemUpdate) { itemUpdate = false; }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 3, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readBaseFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
        paintType = PaintType.getPaintTypeFromUKey(compound.getInteger(TAG_PAINT_TYPE));
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
    
    @Override
    public int getColour(int side) {
        return this.colour;
    }
    
    @Override
    public ICubeColour getColour() {
        return new CubeColour(colour);
    }
    
    @Override
    public void setColour(int colour) {
        setColour(colour, false);
    }

    @Override
    public void setColour(int colour, int side) {
        setColour(colour, false);
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        //NO-OP
        //setColour(colour.g);
    }
    
    @Override
    public void setPaintType(PaintType paintType, int side) {
        this.paintType = paintType;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public PaintType getPaintType(int side) {
        return paintType;
    }
}
