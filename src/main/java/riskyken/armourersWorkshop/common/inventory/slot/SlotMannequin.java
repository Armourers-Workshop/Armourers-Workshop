package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
        case SKIRT:
            if (item instanceof ItemSkin &&((ItemSkin)item)
                    .getSkinType(stack) == SkinTypeRegistry.skinSkirt) {
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
        }
        return false;
    }
}
