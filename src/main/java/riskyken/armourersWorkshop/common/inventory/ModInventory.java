package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.utils.NBTHelper;

public class ModInventory implements IInventory {

    private static final String TAG_ITEMS = "items";
    
    private final String name;
    private final ItemStack[] slots;
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
        this.slots = new ItemStack[slotCount];
        this.parent = parent;
        this.callback = callback;
    }
    
    @Override
    public int getSizeInventory() {
        return this.slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        return this.slots[slotId];
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack itemstack = getStackInSlot(slotId);
        if (itemstack != null) {
            if (itemstack.stackSize <= count){
                setInventorySlotContents(slotId, null);
            }else{
                itemstack = itemstack.splitStack(count);
                setInventorySlotContents(slotId, getStackInSlot(slotId));
                markDirty();
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotId) {
        return getStackInSlot(slotId);
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack stack) {
        this.slots[slotId] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
        if (callback != null) {
            callback.setInventorySlotContents(this, slotId, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return this.name;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return !player.isDead;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
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
}
