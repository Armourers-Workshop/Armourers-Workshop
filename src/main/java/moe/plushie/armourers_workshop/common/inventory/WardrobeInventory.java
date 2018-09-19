package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class WardrobeInventory implements IInventory {
    
    private ItemStack[] wardrobeItemStacks = new ItemStack[ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE];
    private boolean inventoryChanged;
    private final IInventorySlotUpdate callback;
    private final ISkinType skinType;
    
    public WardrobeInventory(IInventorySlotUpdate callback, ISkinType skinType) {
        this.callback = callback;
        this.skinType = skinType;
        for (int i = 0; i < wardrobeItemStacks.length; i++) {
            wardrobeItemStacks[i] = ItemStack.EMPTY;
        }
    }
    
    public ISkinType getSkinType() {
        return skinType;
    }
    
    @Override
    public int getSizeInventory() {
        return wardrobeItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return wardrobeItemStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack itemstack = getStackInSlot(slot);
        
        if (itemstack != null) {
            if (itemstack.getCount() <= count){
                setInventorySlotContents(slot, null);
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
    public void setInventorySlotContents(int slot, ItemStack stack) {
        wardrobeItemStacks[slot] = stack;
        if (stack != null && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        callback.setInventorySlotContents(this, slot, stack);
        markDirty();
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        inventoryChanged = true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    
    public void writeItemsToNBT(NBTTagCompound compound) {
        NBTHelper.writeStackArrayToNBT(compound, skinType.getRegistryName(), wardrobeItemStacks);
    }
    
    public void readItemsFromNBT(NBTTagCompound compound) {
        NBTHelper.readStackArrayFromNBT(compound, skinType.getRegistryName(), wardrobeItemStacks);
    }
    
    public void dropItems(EntityPlayer player) {
        World world = player.getEntityWorld();
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                float f = 0.7F;
                double xV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double yV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double zV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, (double)x + xV, (double)y + yV, (double)z + zV, stack);
                world.spawnEntity(entityitem);
                setInventorySlotContents(i, null);
            }
        }
    }

    @Override
    public String getName() {
        return "pasta";
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
        for (int i = 0; i < wardrobeItemStacks.length; i++) {
            setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }
}
