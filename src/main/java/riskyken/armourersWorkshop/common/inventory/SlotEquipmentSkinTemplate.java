package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;

public class SlotEquipmentSkinTemplate extends Slot {
    
    public SlotEquipmentSkinTemplate(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkinTemplate) {
            return true;
        }
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            return true;
        }
        return false;
    }
}
