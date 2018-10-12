package moe.plushie.armourers_workshop.common.inventory;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ModTileContainer<TILETYPE extends TileEntity> extends Container {

    protected final InventoryPlayer invPlayer;
    protected final TILETYPE tileEntity;
    private int playerInvStartIndex;
    private int playerInvEndIndex;
    
    public ModTileContainer(InventoryPlayer invPlayer, TILETYPE tileEntity) {
        this.invPlayer = invPlayer;
        this.tileEntity = tileEntity;
    }
    
    protected void addPlayerSlots(int posX, int posY) {
        playerInvStartIndex = inventorySlots.size();
        int playerInvY = posY;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, posX + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, posX + 18 * x, playerInvY + y * 18));
            }
        }
        playerInvEndIndex  = inventorySlots.size();
    }
    
    public int getPlayerInvStartIndex() {
        return playerInvStartIndex;
    }
    
    public int getPlayerInvEndIndex() {
        return playerInvEndIndex;
    }
    
    public boolean canSlotHoldItem(int slotIndex, ItemStack itemStack) {
        Slot slot = getSlot(slotIndex);
        return canSlotHoldItem(slot, itemStack);
    }
    
    public boolean canSlotHoldItem(Slot slot, ItemStack itemStack) {
        return slot.isEnabled() & slot.isItemValid(itemStack);
    }

    
    public boolean isSlotPlayerInv(int index) {
        return index >= playerInvStartIndex & index < playerInvEndIndex;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (!isSlotPlayerInv(index)) {
            Slot slot = getSlot(index);
            if (slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemStack result = stack.copy();
                // Moving from tile entity to player.
                if (!this.mergeItemStack(stack, playerInvStartIndex + 9, playerInvEndIndex, false)) {
                    if (!this.mergeItemStack(stack, playerInvStartIndex, playerInvStartIndex + 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                if (stack.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
                slot.onTake(playerIn, stack);
                return result;
            }
            return ItemStack.EMPTY;
        } else {
            return transferStackFromPlayer(playerIn, index);
        }
    }
    
    @Nonnull
    protected abstract ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index);
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead & playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
    }
    
    public TILETYPE getTileEntity() {
        return tileEntity;
    }
}
