package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkinTemplate;
import moe.plushie.armourers_workshop.common.items.ItemArmourContainerItem;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArmourer extends Container {
    
    private TileEntityArmourer armourerBrain;

    public ContainerArmourer(InventoryPlayer invPlayer, TileEntityArmourer armourerBrain) {
        this.armourerBrain = armourerBrain;

        addSlotToContainer(new SlotSkinTemplate(armourerBrain, 0, 64, 21));
        addSlotToContainer(new SlotOutput(armourerBrain, 1, 147, 21));

        int playerInvY = 142;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        Slot slot = getSlot(slotID);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotID < 2) {
                if (!this.mergeItemStack(stack, 11, 38, false)) {
                    if (!this.mergeItemStack(stack, 2, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                if ((
                        stack.getItem() instanceof ItemSkinTemplate & stack.getItemDamage() == 0) |
                        stack.getItem() instanceof ItemSkin |
                        stack.getItem() instanceof ItemArmourContainerItem) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stack);

            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return armourerBrain.isUsableByPlayer(player);
    }

    public TileEntityArmourer getTileEntity() {
        return armourerBrain;
    }
}
