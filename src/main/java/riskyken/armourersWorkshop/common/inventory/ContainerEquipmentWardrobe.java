package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.custom.equipment.PlayerCustomEquipmentData;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;

public class ContainerEquipmentWardrobe extends Container {
    
    PlayerCustomEquipmentData customEquipmentData;
    
    public ContainerEquipmentWardrobe(InventoryPlayer invPlayer, PlayerCustomEquipmentData customEquipmentData) {
        this.customEquipmentData = customEquipmentData;
        
        addSlotToContainer(new SlotEquipmentSkin(ArmourType.HEAD, customEquipmentData, 0, 37, 18));
        addSlotToContainer(new SlotEquipmentSkin(ArmourType.CHEST, customEquipmentData, 1, 37, 45));
        addSlotToContainer(new SlotOutput(customEquipmentData, 5, 37, 72));
        addSlotToContainer(new SlotEquipmentSkin(ArmourType.LEGS, customEquipmentData, 2, 123, 18));
        addSlotToContainer(new SlotEquipmentSkin(ArmourType.SKIRT, customEquipmentData, 3, 123, 45));
        addSlotToContainer(new SlotEquipmentSkin(ArmourType.FEET, customEquipmentData, 4, 123, 72));
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 152));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 94 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId < 6) {
                if (!this.mergeItemStack(stack, 15, 42, false)) {
                    if (!this.mergeItemStack(stack, 6, 15, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemEquipmentSkin) {
                    switch (stack.getItemDamage()) {
                    case 0:
                        if (!this.mergeItemStack(stack, 0, 1, false)) {
                            return null;
                        }
                        break;
                    case 1:
                        if (!this.mergeItemStack(stack, 1, 2, false)) {
                            return null;
                        }
                        break;
                    case 2:
                        if (!this.mergeItemStack(stack, 3, 4, false)) {
                            return null;
                        }
                        break;
                    case 3:
                        if (!this.mergeItemStack(stack, 4, 5, false)) {
                            return null;
                        }
                        break;
                    case 4:
                        if (!this.mergeItemStack(stack, 5, 6, false)) {
                            return null;
                        }
                        break;
                    default:
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

}
