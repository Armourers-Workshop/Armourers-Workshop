
package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.inventory.slot.SlotSkin;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.wardrobe.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.wardrobe.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerSkinWardrobe extends Container {

    private ExPropsPlayerSkinData customEquipmentData;
    private int slotsUnlocked;

    private int indexSkinsStart = 0;
    private int indexSkinsEnd = 0;

    private int indexOutfitStart = 0;
    private int indexOutfitEnd = 0;

    public ContainerSkinWardrobe(InventoryPlayer invPlayer, ExPropsPlayerSkinData customEquipmentData) {
        this.customEquipmentData = customEquipmentData;

        EquipmentWardrobeData ewd = customEquipmentData.getEquipmentWardrobeData();

        WardrobeInventoryContainer wardrobeInvContainer = customEquipmentData.getWardrobeInventoryContainer();

        WardrobeInventory headInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinHead);
        WardrobeInventory chestInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinChest);
        WardrobeInventory legsInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinLegs);
        WardrobeInventory feetInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinFeet);
        WardrobeInventory wingInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinWings);
        
        WardrobeInventory swordInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinSword);
        WardrobeInventory shieldInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinShield);
        WardrobeInventory bowInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinBow);

        WardrobeInventory pickaxeInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinPickaxe);
        WardrobeInventory axeInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinAxe);
        WardrobeInventory shovelInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinShovel);
        WardrobeInventory hoeInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinHoe);

        WardrobeInventory outfitInv = wardrobeInvContainer.getInventoryForSkinType(SkinTypeRegistry.skinOutfit);

        for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinHead)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHead, headInv, i, 83 + i * 19, 27));
                indexSkinsEnd += 1;
            }
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinChest)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinChest, chestInv, i, 83 + i * 19, 46));
                indexSkinsEnd += 1;
            }
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinLegs)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinLegs, legsInv, i, 83 + i * 19, 65));
                indexSkinsEnd += 1;
            }
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinFeet)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinFeet, feetInv, i, 83 + i * 19, 84));
                indexSkinsEnd += 1;
            }
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinWings)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinWings, wingInv, i, 83 + i * 19, 103));
                indexSkinsEnd += 1;
            }
        }

        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 0, 83, 122));
        // addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShield, shieldInv, 0, 102, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinBow, bowInv, 0, 121, 122));

        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinPickaxe, pickaxeInv, 0, 159, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinAxe, axeInv, 0, 178, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShovel, shovelInv, 0, 197, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHoe, hoeInv, 0, 216, 122));
        indexSkinsEnd += 6;

        indexOutfitStart = indexSkinsEnd;
        indexOutfitEnd = indexSkinsEnd;
        for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
            if (i < ewd.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinOutfit)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinOutfit, outfitInv, i, 83  + i * 19, 27));
                indexOutfitEnd += 1;
            }
        }

        int playerInvX = 59;
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

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead & customEquipmentData.getPlayer().equals(player);
    }

    public int getIndexSkinsStart() {
        return indexSkinsStart;
    }

    public int getIndexSkinsEnd() {
        return indexSkinsEnd;
    }

    public int getIndexOutfitStart() {
        return indexOutfitStart;
    }

    public int getIndexOutfitEnd() {
        return indexOutfitEnd;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId < indexOutfitEnd) {
                // Moving item to main inv
                if (!this.mergeItemStack(stack, indexOutfitEnd + 9, indexOutfitEnd + 36, false)) {
                    // Moving item to hotbar
                    if (!this.mergeItemStack(stack, indexOutfitEnd, indexOutfitEnd + 9, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                    boolean slotted = false;
                    for (int i = 0; i < indexOutfitEnd; i++) {
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
