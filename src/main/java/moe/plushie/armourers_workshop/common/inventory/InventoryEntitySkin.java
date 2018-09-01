package moe.plushie.armourers_workshop.common.inventory;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryEntitySkin implements IInventory {
    
    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";
    private final ItemStack[] skinSlots;
    private final IInventorySlotUpdate callback;
    private final ArrayList<ISkinType> skinTypes;
    
    public InventoryEntitySkin(IInventorySlotUpdate callback, ArrayList<ISkinType> skinTypes) {
        this.skinSlots = new ItemStack[skinTypes.size()];
        this.callback = callback;
        this.skinTypes = skinTypes;
    }
    
    public ArrayList<ISkinType> getSkinTypes() {
        return skinTypes;
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
            if (itemstack.getCount() <= count){
                setInventorySlotContents(slotId, null);
            }else{
                itemstack = itemstack.splitStack(count);
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
        skinSlots[slotId] = stack;
        if (stack != null && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
        if (callback != null) {
            callback.setInventorySlotContents(this, slotId, stack);
        }
    }

    @Override
    public String getName() {
        return "skinInventory";
    }

    @Override
    public boolean hasCustomName() {
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
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }
    
    @Override
    public void openInventory(EntityPlayer player) {}
    
    @Override
    public void closeInventory(EntityPlayer player) {}

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
        for (int i = 0; i < skinSlots.length; i++) {
            skinSlots[i] = null;
        }
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)items.getCompoundTagAt(i);
            int slot = item.getByte(TAG_SLOT);
            if (slot >= 0 && slot < getSizeInventory()) {
                //setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getField(int id) {
        // TODO Auto-generated method stub
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
