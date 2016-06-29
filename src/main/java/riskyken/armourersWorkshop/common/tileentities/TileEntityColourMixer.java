package riskyken.armourersWorkshop.common.tileentities;

import java.awt.Color;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.paintingtool.ItemColourPicker;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;


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
                paintingTool.setToolPaintType(stackInput, getPaintType(null));
            }
            if (stackInput.getItem() == ModItems.colourPicker) {
                setPaintType(((ItemColourPicker)stackInput.getItem()).getToolPaintType(stackInput), null);
                setColour(((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput), true);
            }
            markDirty();
        }
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return super.shouldRefresh(world, pos, oldState, newSate);
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
        setPaintType(paintType, null);
        worldObj.markChunkDirty(pos, this);
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
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
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
        if (itemUpdate) { itemUpdate = false; }
        return compound;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        
        ModLogger.log("sending update");
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        readBaseFromNBT(compound);
        colour = compound.getInteger(LibCommonTags.TAG_COLOUR);
        paintType = PaintType.getPaintTypeFromUKey(compound.getInteger(TAG_PAINT_TYPE));
        itemUpdate = compound.getBoolean(TAG_ITEM_UPDATE);
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
        colourUpdate = true;
        ModLogger.log("got update");
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
    public int getColour(EnumFacing side) {
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
    public void setColour(byte[] rgb, EnumFacing side) {
        setColour(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF).getRGB(), false);
    }
    
    @Deprecated
    @Override
    public void setColour(int colour, EnumFacing side) {
        setColour(colour, false);
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        //NO-OP
        //setColour(colour.g);
    }
    
    @Override
    public void setPaintType(PaintType paintType, EnumFacing side) {
        this.paintType = paintType;
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    @Override
    public PaintType getPaintType(EnumFacing side) {
        return paintType;
    }
}
