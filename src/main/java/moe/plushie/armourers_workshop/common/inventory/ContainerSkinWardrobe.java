
package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotDyeBottle;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.items.ItemDyeBottle;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ContainerSkinWardrobe extends ModContainer {

    private final EntitySkinCapability skinCapability;
    private final IWardrobeCap wardrobeCapability;
    private final DyeInventory dyeInventory;
    private int slotsUnlocked;
    
    private int indexSkinsStart = 0;
    private int indexSkinsEnd = 0;
    
    private int indexDyeStart = 0;
    private int indexDyeEnd = 0;
    
    private int indexOutfitStart = 0;
    private int indexOutfitEnd = 0;
    
    public ContainerSkinWardrobe(InventoryPlayer invPlayer, EntitySkinCapability skinCapability, IWardrobeCap wardrobeCapability) {
        super(invPlayer);
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
        this.dyeInventory = new DyeInventory(wardrobeCapability, invPlayer.player.getEntityWorld().isRemote);
        SkinInventoryContainer skinInv = skinCapability.getSkinInventoryContainer();
        boolean isPlayer = wardrobeCapability instanceof IPlayerWardrobeCap;
        boolean isCreative = invPlayer.player.capabilities.isCreativeMode;
        
        if (isPlayer) {
            if (ConfigHandler.wardrobeTabSkins | isCreative) {
                IPlayerWardrobeCap playerWardrobe = (IPlayerWardrobeCap) wardrobeCapability;
                
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

                for (int i = 0; i < EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE; i++) {
                    if (i < playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinHead)) {
                        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHead, headInv, i, 70 + i * 20, 27));
                        indexSkinsEnd += 1;
                    }
                    if (i < playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinChest)) {
                        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinChest, chestInv, i, 70 + i * 20, 46));
                        indexSkinsEnd += 1;
                    }
                    if (i < playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinLegs)) {
                        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinLegs, legsInv, i, 70 + i * 20, 65));
                        indexSkinsEnd += 1;
                    }
                    if (i < playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinFeet)) {
                        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinFeet, feetInv, i, 70 + i * 20, 84));
                        indexSkinsEnd += 1;
                    }
                    if (i < playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinWings)) {
                        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinWings, wingInv, i, 70 + i * 20, 103));
                        indexSkinsEnd += 1;
                    }
                }

                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinSword, swordInv, 0, 70, 122));
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShield, shieldInv, 0, 90, 122));
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinBow, bowInv, 0, 110, 122));

                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinPickaxe, pickaxeInv, 0, 150, 122));
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinAxe, axeInv, 0, 170, 122));
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinShovel, shovelInv, 0, 190, 122));
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinHoe, hoeInv, 0, 210, 122));
                indexSkinsEnd += 7;
            }
        } else {
            ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
            for (int i = 0; i < skinTypes.length; i++) {
                if (skinTypes[i] == SkinTypeRegistry.skinOutfit) {
                    continue;
                }
                for (int j = 0; j < skinCapability.getSlotCountForSkinType(skinTypes[i]); j++) {
                    if (skinTypes[i].getVanillaArmourSlotId() != -1 | skinTypes[i] == SkinTypeRegistry.skinWings) {
                        addSlotToContainer(new SlotSkin(skinTypes[i], skinCapability.getSkinInventoryContainer().getSkinTypeInv(skinTypes[i]), j, 70 + j * 20, 27 + i * 19));
                    } else {
                        addSlotToContainer(new SlotSkin(skinTypes[i], skinCapability.getSkinInventoryContainer().getSkinTypeInv(skinTypes[i]), j, 70 + (i - 5) * 20, 122 + j * 19));
                    }
                    indexSkinsEnd++;
                }
            }
        }
        
        indexDyeStart = indexSkinsEnd;
        indexDyeEnd = indexSkinsEnd;
        if (!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabDyes | isCreative))) {
            for (int i = 0; i < 8; i++) {
                addSlotToContainer(new SlotDyeBottle(dyeInventory, i, 70 + 20 * i, 27));
                indexDyeEnd += 1;
            }
        }
        
        indexOutfitStart = indexDyeEnd;
        indexOutfitEnd = indexDyeEnd;
        if (!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabOutfits | isCreative))) {
            WardrobeInventory invOutfit = skinInv.getSkinTypeInv(SkinTypeRegistry.skinOutfit);
            int outfitSlots = invOutfit.getSizeInventory();
            if (isPlayer) {
                IPlayerWardrobeCap playerWardrobe = (IPlayerWardrobeCap) wardrobeCapability;
                outfitSlots = playerWardrobe.getUnlockedSlotsForSkinType(SkinTypeRegistry.skinOutfit);
            }
            for (int i = 0; i < outfitSlots; i++) {
                int y = 20 * (MathHelper.floor((float)i / 8F));
                int x = 20 * i - (y * 8);
                addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinOutfit, invOutfit, i, 70 + x, 27 + y));
                indexOutfitEnd += 1;
            }
        }

        addPlayerSlots(38, 158);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead;
    }
    
    public int getIndexSkinsStart() {
        return indexSkinsStart;
    }
    
    public int getIndexSkinsEnd() {
        return indexSkinsEnd;
    }
    
    public int getIndexDyeStart() {
        return indexDyeStart;
    }
    
    public int getIndexDyeEnd() {
        return indexDyeEnd;
    }
    
    public int getIndexOutfitStart() {
        return indexOutfitStart;
    }
    
    public int getIndexOutfitEnd() {
        return indexOutfitEnd;
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
                for (int i = indexSkinsStart; i < indexSkinsEnd; i++) {
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
                    for (int i = indexDyeStart; i < indexDyeEnd; i++) {
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
            
            // TODO Add check for valid outfit.
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = indexOutfitStart; i < indexOutfitEnd; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
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

    private class DyeInventory extends ModInventory {

        private final IWardrobeCap wardrobeCapability;
        private final boolean isRemote;

        public DyeInventory(IWardrobeCap wardrobeCapability, boolean isRemote) {
            super("dyeInventory", 8);
            this.wardrobeCapability = wardrobeCapability;
            this.isRemote = isRemote;
            ISkinDye dye = wardrobeCapability.getDye();
            for (int i = 0; i < 8; i++) {
                if (dye.haveDyeInSlot(i)) {
                    byte[] rgbt = dye.getDyeColour(i);
                    ItemStack bottle = new ItemStack(ModItems.dyeBottle, 1, 1);
                    PaintingHelper.setToolPaintColour(bottle, rgbt);
                    PaintingHelper.setToolPaint(bottle, PaintRegistry.getPaintTypeFormByte(rgbt[3]));
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
            if (!isRemote) {
                wardrobeCapability.syncToAllTracking();
            }
        }
    }
}
