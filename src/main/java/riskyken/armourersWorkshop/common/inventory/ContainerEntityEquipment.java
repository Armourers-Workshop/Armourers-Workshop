package riskyken.armourersWorkshop.common.inventory;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.slot.SlotSkin;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerEntityEquipment extends Container {

    private int skinSlots = 0;
    
    public ContainerEntityEquipment(InventoryPlayer invPlayer, InventoryEntitySkin skinInventory) {
        ArrayList<ISkinType> skinTypes = skinInventory.getSkinTypes();
        for (int i = 0; i < skinTypes.size(); i++) {
            addSlotToContainer(new SlotSkin(skinTypes.get(i), skinInventory, i, 8 + i * 18, 21));
            skinSlots++;
        }
        
        int hotBarY = 124;
        int playerInvY = 66;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return !entityPlayer.isDead;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId < skinSlots) {
                //Moving item to main inv
                if (!this.mergeItemStack(stack, skinSlots + 9, skinSlots + 36, false)) {
                    //Moving item to hotbar
                    if (!this.mergeItemStack(stack, skinSlots, skinSlots + 9, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                    boolean slotted = false;
                    for (int i = 0; i < skinSlots; i++) {
                        Slot targetSlot = getSlot(i);
                        if (targetSlot.isItemValid(stack)) {
                            if (this.mergeItemStack(stack, i, i + 1, false)) {
                                slotted = true;
                                break;
                            }
                        }
                    }
                    if (!slotted) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (stack.getCount() == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(entityPlayer, stack);

            return result;
        }
        return null;
    }
}
