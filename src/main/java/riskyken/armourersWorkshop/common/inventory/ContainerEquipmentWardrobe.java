
package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerEquipmentWardrobe extends Container {
    
    private ExPropsPlayerEquipmentData customEquipmentData;
    private int slotsUnlocked;
    private int skinSlots = 0;
    
    public ContainerEquipmentWardrobe(InventoryPlayer invPlayer, ExPropsPlayerEquipmentData customEquipmentData) {
        this.customEquipmentData = customEquipmentData;
        
        slotsUnlocked = customEquipmentData.getEquipmentWardrobeData().slotsUnlocked;
        
        WardrobeInventoryContainer wardrobeInvContainer = customEquipmentData.getWardrobeInventoryContainer();
        
        WardrobeInventory headInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinHead);
        WardrobeInventory chestInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinChest);
        WardrobeInventory legsInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinLegs);
        WardrobeInventory feetInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinFeet);
        WardrobeInventory swordInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinSword);
        WardrobeInventory bowInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinBow);
        WardrobeInventory arrowInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinArrow);
        
        
        for (int i = 0; i < slotsUnlocked; i++) {
            addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinHead, headInv, i, 88 + i * 19, 18));
            addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinChest, chestInv, i, 88 + i * 19, 37));
            addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinLegs, legsInv, i, 88 + i * 19, 56));
            addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinFeet, feetInv, i, 88 + i * 19, 75));
            skinSlots += 4;
        }
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinSword, swordInv, 0, 29, 113));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinBow, bowInv, 0, 49, 113));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinArrow, arrowInv, 0, 69, 113));
        skinSlots += 3;
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 54 + 18 * x, 232));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 54 + 18 * x, 174 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead & player == customEquipmentData.getPlayer();
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
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
                if (stack.getItem() instanceof ItemEquipmentSkin & SkinNBTHelper.stackHasSkinData(stack)) {
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
