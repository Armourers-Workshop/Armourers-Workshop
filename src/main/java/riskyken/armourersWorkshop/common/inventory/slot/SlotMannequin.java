package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.common.inventory.MannequinSlotType;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

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
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 0) {
                return true;
            }
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinHead) {
                return true;
            }
            break;
        case CHEST:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 1) {
                return true;
            }
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinChest) {
                return true;
            }
            break;
        case LEGS:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 2) {
                return true;
            }
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinLegs) {
                return true;
            }
            break;
        case FEET:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 3) {
                return true;
            }
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinFeet) {
                return true;
            }
            break;
            
        case LEFT_HAND:
            return true;
        case RIGHT_HAND:
            return true;
        case WINGS:
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinWings) {
                return true;
            }
            break;
        }
        return false;
    }
    
    @Override
    public IIcon getBackgroundIconIndex() {
        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        switch (slotType) {
        case HEAD:
            return str.skinHead.getEmptySlotIcon();
        case CHEST:
            return str.skinChest.getEmptySlotIcon();
        case LEGS:
            return str.skinLegs.getEmptySlotIcon();
        case FEET:
            return str.skinFeet.getEmptySlotIcon();
        case LEFT_HAND:
            return str.skinBow.getEmptySlotIcon();
        case RIGHT_HAND:
            return str.skinSword.getEmptySlotIcon();
        case WINGS:
            return str.skinWings.getEmptySlotIcon();
        }
        return super.getBackgroundIconIndex();
    }
}
