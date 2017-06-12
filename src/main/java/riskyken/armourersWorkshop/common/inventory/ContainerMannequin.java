package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.inventory.slot.SlotMannequin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerMannequin extends Container {

    private TileEntityMannequin tileEntity;
    
    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        this.tileEntity = tileEntity;
        
        for (int y = 0; y < MannequinSlotType.values().length; y++) {
            addSlotToContainer(new SlotMannequin(MannequinSlotType.getOrdinal(y) ,tileEntity, y, 11, 25 + 19 * y));
        }
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 8 + 18 * x, 232));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 8 + 18 * x, 174 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId < 7) {
                // Moving from mannequin to player.
                if (!this.mergeItemStack(stack, 16, 43, false)) {
                    if (!this.mergeItemStack(stack, 7, 16, false)) {
                        return null;
                    }
                }
            } else {
                // Moving from player to mannequin.
                boolean slotted = false;
                for (int i = 0; i < 7; i++) {
                    Slot targetSlot = getSlot(i);
                    
                    ISkinType skinType = SkinNBTHelper.getSkinTypeFromStack(stack);
                    
                    if (skinType != null && skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                        
                        if (i != 4 & i != 5) {
                            if (targetSlot.isItemValid(stack)) {
                                if (this.mergeItemStack(stack, i, i + 1, false)) {
                                    slotted = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (targetSlot.isItemValid(stack)) {
                            if (this.mergeItemStack(stack, i, i + 1, false)) {
                                slotted = true;
                                break;
                            }
                        }
                    }
                }
                if (!slotted) {
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
        return tileEntity.isUseableByPlayer(player);
    }
    
    public TileEntityMannequin getTileEntity() {
        return tileEntity;
    }
}
