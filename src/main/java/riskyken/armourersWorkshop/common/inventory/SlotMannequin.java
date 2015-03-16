package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;

public class SlotMannequin extends Slot {

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
            if (item instanceof ItemEquipmentSkin &&((ItemEquipmentSkin)item)
                    .getEquipmentType(stack) == EnumEquipmentType.HEAD) {
                return true;
            }
            break;
        case CHEST:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 1) {
                return true;
            }
            if (item instanceof ItemEquipmentSkin &&((ItemEquipmentSkin)item)
                    .getEquipmentType(stack) == EnumEquipmentType.CHEST) {
                return true;
            }
            break;
        case LEGS:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 2) {
                return true;
            }
            if (item instanceof ItemEquipmentSkin &&((ItemEquipmentSkin)item)
                    .getEquipmentType(stack) == EnumEquipmentType.LEGS) {
                return true;
            }
            break;
        case SKIRT:
            if (item instanceof ItemEquipmentSkin &&((ItemEquipmentSkin)item)
                    .getEquipmentType(stack) == EnumEquipmentType.SKIRT) {
                return true;
            }
            break;
        case FEET:
            if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 3) {
                return true;
            }
            if (item instanceof ItemEquipmentSkin &&((ItemEquipmentSkin)item)
                    .getEquipmentType(stack) == EnumEquipmentType.FEET) {
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
