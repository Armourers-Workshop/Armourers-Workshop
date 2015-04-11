package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;

public class ContainerEquipmentWardrobe extends Container {
    
    private ExtendedPropsPlayerEquipmentData customEquipmentData;
    
    public ContainerEquipmentWardrobe(InventoryPlayer invPlayer, ExtendedPropsPlayerEquipmentData customEquipmentData) {
        this.customEquipmentData = customEquipmentData;
        
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.HEAD, customEquipmentData, 0, 88, 18));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.CHEST, customEquipmentData, 1, 88, 37));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.SWORD, customEquipmentData, 5, 69, 113));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.LEGS, customEquipmentData, 2, 88, 75));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.SKIRT, customEquipmentData, 3, 88, 56));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.FEET, customEquipmentData, 4, 88, 94));
        addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.BOW, customEquipmentData, 6, 28, 113));

        addSlotToContainer(new SlotColourTool(customEquipmentData, 7, 91, 35));
        addSlotToContainer(new SlotOutput(customEquipmentData, 8, 130, 35));
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 54 + 18 * x, 232));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 54 + 18 * x, 174 + y * 18));
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

            if (slotId < 8) {
                if (!this.mergeItemStack(stack, 17, 44, false)) {
                    if (!this.mergeItemStack(stack, 8, 17, false)) {
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
                    case 5:
                        if (!this.mergeItemStack(stack, 2, 3, false)) {
                            return null;
                        }
                        break;
                    case 6:
                        if (!this.mergeItemStack(stack, 6, 7, false)) {
                            return null;
                        }
                        break;    
                    default:
                        return null;
                    }
                } else if(stack.getItem() instanceof ItemColourPicker) {
                    if (!this.mergeItemStack(stack, 7, 8, false)) {
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
