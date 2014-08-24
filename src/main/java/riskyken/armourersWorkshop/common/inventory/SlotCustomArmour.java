package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemCustomArmour;

public class SlotCustomArmour extends Slot {
    
    public SlotCustomArmour(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemCustomArmour;
    }
}
