package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import riskyken.armourersWorkshop.utils.NBTHelper;

public abstract class AbstractTileEntityInventory extends ModTileEntity implements IInventory {

    private static final String TAG_ITEMS = "items";
    protected final ItemStack[] items;
    
    public AbstractTileEntityInventory(int inventorySize) {
        this.items = new ItemStack[inventorySize];
    }
    
    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items[i];
    }
    
    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);
        
        if (itemstack != null) {
            if (itemstack.stackSize <= count){
                setInventorySlotContents(i, null);
            }else{
                itemstack = itemstack.splitStack(count);
                markDirty();
            }
        }
        return itemstack;
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack item = getStackInSlot(index);
        setInventorySlotContents(index, null);
        return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        items[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }
    
    @Override
    public void openInventory(EntityPlayer player) {}
    
    @Override
    public void closeInventory(EntityPlayer player) {}
    
    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return entityplayer.getDistanceSq(pos) <= 64;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeItemsToNBT(compound);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readItemsFromNBT(compound);
    }
    
    public void writeBaseToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }
    
    public void readBaseFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
    
    public void readCommonFromNBT(NBTTagCompound compound) {
    }
    
    public void writeCommonToNBT(NBTTagCompound compound) {
    }
    
    public void writeItemsToNBT(NBTTagCompound compound) {
        NBTHelper.writeStackArrayToNBT(compound, TAG_ITEMS, items);
    }
    
    public void readItemsFromNBT(NBTTagCompound compound) {
        NBTHelper.readStackArrayFromNBT(compound, TAG_ITEMS, items);
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }
    
    @Override
    public int getField(int id) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getFieldCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void setField(int id, int value) {
        // TODO Auto-generated method stub
        
    }
}
