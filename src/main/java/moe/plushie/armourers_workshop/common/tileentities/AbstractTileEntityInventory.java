package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractTileEntityInventory extends ModTileEntity implements IInventory {

    private static final String TAG_ITEMS = "items";
    protected final NonNullList<ItemStack> items;
    
    public AbstractTileEntityInventory(int inventorySize) {
        this.items = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
    }
    
    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items.get(i);
    }
    
    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);
        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() <= count){
                setInventorySlotContents(i, ItemStack.EMPTY);
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
        setInventorySlotContents(index, ItemStack.EMPTY);
        return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        items.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
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
        return 0;
    }
    
    @Override
    public void clear() {
        items.clear();
    }
    
    @Override
    public void closeInventory(EntityPlayer player) {
    }
    
    @Override
    public void openInventory(EntityPlayer player) {
    }
    
    @Override
    public void setField(int id, int value) {
    }
    
    @Override
    public int getFieldCount() {
        return 0;
    }
    
    @Override
    public String getName() {
        return "";
    }
    
    @Override
    public boolean hasCustomName() {
        return false;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return super.getDisplayName();
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(getPos()) <= 64;
    }
}
