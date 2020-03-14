package moe.plushie.armourers_workshop.common.inventory;

import javax.annotation.Nonnull;

import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ModInventory implements IInventory {

    private static final String TAG_ITEMS = "items";
    
    private final String name;
    protected final NonNullList<ItemStack> slots;
    private final IInventoryCallback callback;
    private final TileEntity parent;
    
    public ModInventory(String name, int slotCount) {
        this(name, slotCount, null, null);
    }
    
    public ModInventory(String name, int slotCount, TileEntity parent) {
        this(name, slotCount, parent, null);
    }
    
    public ModInventory(String name, int slotCount, IInventoryCallback callback) {
        this(name, slotCount, null, callback);
    }
    
    public ModInventory(String name, int slotCount, TileEntity parent, IInventoryCallback callback) {
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
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(slots, index, count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemstack  = ItemStackHelper.getAndRemove(slots, index);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        this.slots.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
        if (callback != null) {
            callback.setInventorySlotContents(this, index, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (callback != null) {
            callback.dirty();
        }
        if (parent != null) {
            parent.markDirty();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack stack) {
        return true;
    }
    
    public NBTTagCompound saveItemsToNBT(NBTTagCompound compound) {
        NBTHelper.writeStackArrayToNBT(compound, TAG_ITEMS, slots);
        return compound;
    }
    
    public void loadItemsFromNBT(NBTTagCompound compound) {
        clear();
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
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        slots.clear();
    }
    
    public interface IInventoryCallback {
        public void setInventorySlotContents(IInventory inventory, int index, @Nonnull ItemStack stack);
        
        public void dirty();
    }
}
