package riskyken.armourersWorkshop.common.equipment.npc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;

public class InventoryEntitySkin implements IInventory {
    
    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";
    private final ItemStack[] skinSlots;
    private IInventorySlotUpdate callback;
    
    public InventoryEntitySkin(IInventorySlotUpdate callback) {
        this.skinSlots = new ItemStack[5];
        this.callback = callback;
    }
    
    @Override
    public int getSizeInventory() {
        return skinSlots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        return skinSlots[slotId];
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack itemstack = getStackInSlot(slotId);
        if (itemstack != null) {
            if (itemstack.stackSize <= count){
                setInventorySlotContents(slotId, null);
            }else{
                itemstack = itemstack.splitStack(count);
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
        skinSlots[slotId] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
        if (callback != null) {
            callback.setInventorySlotContents(slotId, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return "skinInventory";
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
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }
    
    public void saveItemsToNBT(NBTTagCompound compound) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte(TAG_SLOT, (byte)i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag(TAG_ITEMS, items);
    }
    
    public void loadItemsFromNBT(NBTTagCompound compound) {
        NBTTagList items = compound.getTagList(TAG_ITEMS, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)items.getCompoundTagAt(i);
            int slot = item.getByte(TAG_SLOT);
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }
}
