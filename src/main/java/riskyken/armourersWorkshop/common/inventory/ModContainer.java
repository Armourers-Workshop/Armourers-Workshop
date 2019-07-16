package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;

public class ModContainer extends Container {

    private final EntityPlayer player;

    private int playerInvStartIndex;
    private int playerInvEndIndex;

    public ModContainer(EntityPlayer player) {
        this.player = player;
    }

    protected void addPlayerSlots(int posX, int posY) {
        playerInvStartIndex = inventorySlots.size();
        int playerInvY = posY;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(player.inventory, x, posX + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(player.inventory, x + y * 9 + 9, posX + 18 * x, playerInvY + y * 18));
            }
        }
        playerInvEndIndex = inventorySlots.size();
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public int getPlayerInvStartIndex() {
        return playerInvStartIndex;
    }

    public int getPlayerInvEndIndex() {
        return playerInvEndIndex;
    }

    public boolean isSlotPlayerInv(int index) {
        return index >= playerInvStartIndex & index < playerInvEndIndex;
    }

    protected boolean canSlotHoldItem(int slotIndex, ItemStack itemStack) {
        Slot slot = getSlot(slotIndex);
        return canSlotHoldItem(slot, itemStack);
    }

    protected boolean canSlotHoldItem(Slot slot, ItemStack itemStack) {
        return slot.isItemValid(itemStack);
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
                        return null;
                    }
                }
                if (stack.stackSize == 0) {
                    slot.putStack(null);
                } else {
                    slot.onSlotChanged();
                }
                slot.onPickupFromSlot(playerIn, stack);
                return result;
            }
            return null;
        } else {
            return transferStackFromPlayer(playerIn, index);
        }
    }

    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead;
    }
}
