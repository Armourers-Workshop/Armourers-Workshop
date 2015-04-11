package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;

public class SlotEquipmentSkin extends SlotHidable {
    
    private EnumEquipmentType type;
    
    public SlotEquipmentSkin(EnumEquipmentType type, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.type = type;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            if (stack.getItemDamage() == this.type.ordinal() - 1) {
                return true;
            }
        }
        return false;
    }
}
