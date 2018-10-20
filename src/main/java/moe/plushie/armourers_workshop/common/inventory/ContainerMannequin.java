package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotMannequin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMannequin extends ModTileContainer<TileEntityMannequin> {
    
    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(invPlayer, tileEntity);
        
        addPlayerSlots(8, 174);
        
        for (int i = 0; i < 5; i++) {
            for (int y = 0; y < MannequinSlotType.values().length; y++) {
                addSlotToContainer(new SlotMannequin(MannequinSlotType.getOrdinal(y), tileEntity, y + i * 7, 5 + 19 * y, 5 + i * 19));
            }
        }
    }
    
    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            
            // Moving from player to mannequin.
            boolean slotted = false;
            for (int i = 0; i < TileEntityMannequin.CONS_INVENTORY_SIZE; i++) {
                int targetSlotId = i + 36;
                Slot targetSlot = getSlot(targetSlotId);
                boolean handSlot = false;
                if (i % 7 == 4) {
                    handSlot = true;
                }
                if (i % 7 == 5) {
                    handSlot = true;
                }
                
                ISkinType skinType = SkinNBTHelper.getSkinTypeFromStack(stack);
                
                if (skinType != null && skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                    if (!handSlot) {
                        if (targetSlot.isItemValid(stack)) {
                            if (this.mergeItemStack(stack, targetSlotId, targetSlotId + 1, false)) {
                                slotted = true;
                                break;
                            }
                        }
                    }
                } else {
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, targetSlotId, targetSlotId + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }
            if (!slotted) {
                return ItemStack.EMPTY;
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
    }
}
