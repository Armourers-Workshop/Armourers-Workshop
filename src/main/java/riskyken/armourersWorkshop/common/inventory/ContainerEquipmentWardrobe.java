package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotColourTool;
import riskyken.armourersWorkshop.common.inventory.slot.SlotEquipmentSkin;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class ContainerEquipmentWardrobe extends Container {
    
    private ExPropsPlayerEquipmentData customEquipmentData;
    
    public ContainerEquipmentWardrobe(InventoryPlayer invPlayer, ExPropsPlayerEquipmentData customEquipmentData) {
        this.customEquipmentData = customEquipmentData;
        
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinHead, customEquipmentData, 0, 88, 18));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinChest, customEquipmentData, 1, 88, 37));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinLegs, customEquipmentData, 2, 88, 56));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinFeet, customEquipmentData, 3, 88, 75));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinSword, customEquipmentData, 4, 29, 113));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinBow, customEquipmentData, 5, 49, 113));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinArrow, customEquipmentData, 6, 69, 113));
        

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
                if (stack.getItem() instanceof ItemEquipmentSkin & EquipmentNBTHelper.stackHasSkinData(stack)) {
                    boolean slotted = false;
                    for (int i = 0; i < 7; i++) {
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
