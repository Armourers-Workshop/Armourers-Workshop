package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import moe.plushie.armourers_workshop.common.inventory.MannequinSlotType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotMannequin extends SlotHidable {

    private MannequinSlotType slotType;

    public SlotMannequin(MannequinSlotType slotType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.slotType = slotType;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        Item item = stack.getItem();

        switch (slotType) {
        case HEAD:
            if (item instanceof ItemBlock) {
                return true;
            }
            /*
             * if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 0) { return
             * true; }
             */
            if (item instanceof ItemSkin && ((ItemSkin) item).getSkinType(stack) == SkinTypeRegistry.skinHead) {
                return true;
            }
            break;
        case CHEST:
            /*
             * if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 1) { return
             * true; }
             */
            if (item instanceof ItemSkin && ((ItemSkin) item).getSkinType(stack) == SkinTypeRegistry.skinChest) {
                return true;
            }
            break;
        case LEGS:
            /*
             * if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 2) { return
             * true; }
             */
            if (item instanceof ItemSkin && ((ItemSkin) item).getSkinType(stack) == SkinTypeRegistry.skinLegs) {
                return true;
            }
            break;
        case FEET:
            /*
             * if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 3) { return
             * true; }
             */
            if (item instanceof ItemSkin && ((ItemSkin) item).getSkinType(stack) == SkinTypeRegistry.skinFeet) {
                return true;
            }
            break;

        case LEFT_HAND:
            return true;
        case RIGHT_HAND:
            return true;
        case WINGS:
            if (item instanceof ItemSkin && ((ItemSkin) item).getSkinType(stack) == SkinTypeRegistry.skinWings) {
                return true;
            }
            break;
        }
        return false;
    }

    public ISkinType getSkinType() {
        switch (slotType) {
        case HEAD:
            return SkinTypeRegistry.skinHead;
        case CHEST:
            return SkinTypeRegistry.skinChest;
        case LEGS:
            return SkinTypeRegistry.skinLegs;
        case FEET:
            return SkinTypeRegistry.skinFeet;
        case LEFT_HAND:
            break;
        case RIGHT_HAND:
            break;
        case WINGS:
            return SkinTypeRegistry.skinWings;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getBackgroundLocation() {
        ISkinType skinType = getSkinType();
        if (skinType != null) {
            return skinType.getSlotIcon();
        }
        return super.getBackgroundLocation();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        ISkinType skinType = getSkinType();
        if (skinType != null) {
            return new DummySprite("");
        }
        return super.getBackgroundSprite();
    }

    @SideOnly(Side.CLIENT)
    private class DummySprite extends TextureAtlasSprite {

        protected DummySprite(String spriteName) {
            super(spriteName);
        }

        @Override
        public float getMaxU() {
            return 1;
        }

        @Override
        public float getMaxV() {
            return 1;
        }

        @Override
        public float getMinU() {
            return 0;
        }

        @Override
        public float getMinV() {
            return 0;
        }
    }

    /*
     * @Override public IIcon getBackgroundIconIndex() { SkinTypeRegistry str =
     * SkinTypeRegistry.INSTANCE; switch (slotType) { case HEAD: return
     * str.skinHead.getEmptySlotIcon(); case CHEST: return
     * str.skinChest.getEmptySlotIcon(); case LEGS: return
     * str.skinLegs.getEmptySlotIcon(); case FEET: return
     * str.skinFeet.getEmptySlotIcon(); case LEFT_HAND: return
     * str.skinBow.getEmptySlotIcon(); case RIGHT_HAND: return
     * str.skinSword.getEmptySlotIcon(); case WINGS: return
     * str.skinWings.getEmptySlotIcon(); } return super.getBackgroundIconIndex(); }
     */
}
