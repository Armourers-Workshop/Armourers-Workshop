
package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.capability.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSkinWardrobe extends Container {
    
    private EntitySkinCapability skinCapability;
    private int slotsUnlocked;
    private int skinSlots = 0;
    
    public ContainerSkinWardrobe(InventoryPlayer invPlayer, EntitySkinCapability skinCapability) {
        this.skinCapability = skinCapability;
        
        //EquipmentWardrobeData ewd = customEquipmentData.getEquipmentWardrobeData();
        
        SkinInventoryContainer skinInvContainer = skinCapability.getSkinInventoryContainer();
        
        WardrobeInventory headInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinHead);
        WardrobeInventory chestInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinChest);
        WardrobeInventory legsInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinLegs);
        WardrobeInventory feetInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinFeet);
        WardrobeInventory swordInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinSword);
        WardrobeInventory bowInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinBow);
        WardrobeInventory wingInv = skinInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinWings);
        
        for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinHead)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHead, headInv, i, 68 + i * 20, 18));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinChest)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinChest, chestInv, i, 68 + i * 20, 37));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinLegs)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinLegs, legsInv, i, 68 + i * 20, 56));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinFeet)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinFeet, feetInv, i, 68 + i * 20, 75));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinWings)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinWings, wingInv, i, 68 + i * 20, 94));
                skinSlots += 1;
            }
        }
        
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 0, 8, 113));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinBow, bowInv, 0, 28, 113));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 1, 48, 113));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 2, 68, 113));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 3, 88, 113));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 4, 108, 113));
        skinSlots += 6;
        
        
        int playerInvX = 38;
        int playerInvY = 158;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, playerInvX + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, playerInvX + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    public int getSkinSlots() {
        return skinSlots;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead & skinCapability.getEntity().equals(player);
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

            slot.onTake(player, stack);

            return result;
        }
        return null;
    }

}
