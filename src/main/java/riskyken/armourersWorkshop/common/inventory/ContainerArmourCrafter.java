package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemCustomArmourTemplate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourCrafter;

public class ContainerArmourCrafter extends Container {

    private TileEntityArmourCrafter armourCrafter;
    
    public ContainerArmourCrafter(InventoryPlayer invPlayer, TileEntityArmourCrafter armourCrafter) {
        this.armourCrafter = armourCrafter;

        addSlotToContainer(new SlotArmourTemplate(armourCrafter, 0, 24, 33));
        addSlotToContainer(new SlotArmour(armourCrafter, 1, 24, 77));
        addSlotToContainer(new SlotOutput(armourCrafter, 2, 132, 55));

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 173));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 115 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotID < 3) {
                if (!this.mergeItemStack(stack, 12, 39, false)) {
                    if (!this.mergeItemStack(stack, 3, 12, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemCustomArmourTemplate) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return null;
                    }
                } else if (stack.getItem() instanceof ItemArmor) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

            return result;
        }

        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return armourCrafter.isUseableByPlayer(player);
    }

    public TileEntityArmourCrafter getTileEntity() {
        return armourCrafter;
    }
}
