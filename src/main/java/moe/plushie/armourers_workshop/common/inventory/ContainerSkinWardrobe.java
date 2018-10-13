
package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCapability;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotDyeBottle;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.items.ItemDyeBottle;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSkinWardrobe extends ModContainer {

    private final EntitySkinCapability skinCapability;
    private final IWardrobeCapability wardrobeCapability;
    private final DyeInventory dyeInventory;
    private int slotsUnlocked;
    private int skinSlots = 0;

    public ContainerSkinWardrobe(InventoryPlayer invPlayer, EntitySkinCapability skinCapability, IWardrobeCapability wardrobeCapability) {
        super(invPlayer);
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
        this.dyeInventory = new DyeInventory(wardrobeCapability);

        SkinInventoryContainer skinInv = skinCapability.getSkinInventoryContainer();

        WardrobeInventory headInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinHead);
        WardrobeInventory chestInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinChest);
        WardrobeInventory legsInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinLegs);
        WardrobeInventory feetInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinFeet);
        WardrobeInventory wingInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinWings);

        WardrobeInventory swordInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinSword);
        WardrobeInventory shieldInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinShield);
        WardrobeInventory bowInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinBow);

        WardrobeInventory pickaxeInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinPickaxe);
        WardrobeInventory axeInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinAxe);
        WardrobeInventory shovelInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinShovel);
        WardrobeInventory hoeInv = skinInv.getSkinTypeInv(SkinTypeRegistry.skinHoe);

        for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinHead)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHead, headInv, i, 70 + i * 20, 27));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinChest)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinChest, chestInv, i, 70 + i * 20, 46));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinLegs)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinLegs, legsInv, i, 70 + i * 20, 65));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinFeet)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinFeet, feetInv, i, 70 + i * 20, 84));
                skinSlots += 1;
            }
            if (i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinWings)) {
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinWings, wingInv, i, 70 + i * 20, 103));
                skinSlots += 1;
            }
        }

        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 0, 70, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShield, shieldInv, 0, 90, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinBow, bowInv, 0, 110, 122));

        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinPickaxe, pickaxeInv, 0, 150, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinAxe, axeInv, 0, 170, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShovel, shovelInv, 0, 190, 122));
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHoe, hoeInv, 0, 210, 122));
        skinSlots += 7;

        for (int i = 0; i < 8; i++) {
            addSlotToContainer(new SlotDyeBottle(dyeInventory, i, 70 + 20 * i, 27));
        }

        addPlayerSlots(38, 158);
    }

    public int getSkinSlots() {
        return skinSlots;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead & skinCapability.getEntity().equals(player);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            boolean slotted = false;
            
            // Putting skin in inv
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = 0; i < skinSlots; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }
            if (stack.getItem() == ModItems.dyeBottle) {
                if (((ItemDyeBottle)stack.getItem()).getToolHasColour(stack)) {
                    for (int i = skinSlots; i < skinSlots + 8; i++) {
                        Slot targetSlot = getSlot(i);
                        if (targetSlot.isItemValid(stack)) {
                            if (this.mergeItemStack(stack, i, i + 1, false)) {
                                slotted = true;
                                break;
                            }
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

    private class DyeInventory extends ModInventory {

        private final IWardrobeCapability wardrobeCapability;

        public DyeInventory(IWardrobeCapability wardrobeCapability) {
            super("dyeInventory", 8);
            this.wardrobeCapability = wardrobeCapability;
            ISkinDye dye = wardrobeCapability.getDye();
            for (int i = 0; i < 8; i++) {
                if (dye.haveDyeInSlot(i)) {
                    byte[] rgbt = dye.getDyeColour(i);
                    ItemStack bottle = new ItemStack(ModItems.dyeBottle, 1, 1);
                    PaintingHelper.setToolPaintColour(bottle, rgbt);
                    PaintingHelper.setToolPaint(bottle, PaintType.getPaintTypeFormSKey(rgbt[3]));
                    if (dye.hasName(i)) {
                        bottle.setStackDisplayName(dye.getDyeName(i));
                    }
                    slots.set(i, bottle);
                } else {
                    slots.set(i, ItemStack.EMPTY);
                }
            }
        }
        
        @Override
        public void setInventorySlotContents(int slotId, ItemStack stack) {
            super.setInventorySlotContents(slotId, stack);
            if (stack.isEmpty()) {
                wardrobeCapability.getDye().removeDye(slotId);
            } else {
                if (PaintingHelper.getToolHasPaint(stack)) {
                    byte[] rgbt = PaintingHelper.getToolPaintData(stack);
                    wardrobeCapability.getDye().addDye(slotId, rgbt);
                }
            }
            wardrobeCapability.syncToAllAround();
        }
    }
}
