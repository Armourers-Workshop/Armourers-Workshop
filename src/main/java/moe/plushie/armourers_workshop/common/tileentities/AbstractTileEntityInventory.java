package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

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
            if (itemstack.getCount() <= count){
                setInventorySlotContents(i, null);
            }else{
                itemstack = itemstack.splitStack(count);
                markDirty();
            }
        }
        return itemstack;
    }

    /*@Override
    public ItemStack getStackInSlotOnClosing(int i) {
        ItemStack item = getStackInSlot(i);
        setInventorySlotContents(i, null);
        return item;
    }*/

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        items[i] = itemstack;
        if (itemstack != null && itemstack.getCount() > getInventoryStackLimit()) {
            itemstack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
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
    public int getField(int id) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void closeInventory(EntityPlayer player) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void openInventory(EntityPlayer player) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setField(int id, int value) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getFieldCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean hasCustomName() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        // TODO Auto-generated method stub
        return super.getDisplayName();
    }
    
    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        // TODO Auto-generated method stub
        //return entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
        return false;
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
        // TODO Auto-generated method stub
        return null;
    }
}
