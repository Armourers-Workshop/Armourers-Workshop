package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ModInventory implements IInventory {

    private static final String TAG_ITEMS = "items";
    
    private final String name;
    private final NonNullList<ItemStack> slots;
    private final IInventorySlotUpdate callback;
    private final TileEntity parent;
    
    public ModInventory(String name, int slotCount) {
        this(name, slotCount, null, null);
    }
    
    public ModInventory(String name, int slotCount, TileEntity parent) {
        this(name, slotCount, parent, null);
    }
    
    public ModInventory(String name, int slotCount, IInventorySlotUpdate callback) {
        this(name, slotCount, null, callback);
    }
    
    public ModInventory(String name, int slotCount, TileEntity parent, IInventorySlotUpdate callback) {
        this.name = name;
        this.slots = NonNullList.<ItemStack>withSize(slotCount, ItemStack.EMPTY);
        this.parent = parent;
        this.callback = callback;
    }
    
    @Override
    public int getSizeInventory() {
        return this.slots.size();
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        return this.slots.get(slotId);
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack itemstack = getStackInSlot(slotId);
        if (itemstack != ItemStack.EMPTY) {
            if (itemstack.getCount() <= count){
                setInventorySlotContents(slotId, ItemStack.EMPTY);
            }else{
                itemstack = itemstack.splitStack(count);
                setInventorySlotContents(slotId, getStackInSlot(slotId));
                markDirty();
            }
        }
        return itemstack;
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return getStackInSlot(index);
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack stack) {
        this.slots.set(slotId, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
        if (callback != null) {
            callback.setInventorySlotContents(this, slotId, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (parent != null) {
            parent.markDirty();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack stack) {
        return true;
    }
    
    public void saveItemsToNBT(NBTTagCompound compound) {
        NBTHelper.writeStackArrayToNBT(compound, TAG_ITEMS, slots);
    }
    
    public void loadItemsFromNBT(NBTTagCompound compound) {
        NBTHelper.readStackArrayFromNBT(compound, TAG_ITEMS, slots);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return !player.isDead;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        return 0;
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
    public void clear() {
        // TODO Auto-generated method stub
    }
}
