package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemCustomArmourTemplate;

public class SlotArmourTemplate extends Slot {
    
    public SlotArmourTemplate(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemCustomArmourTemplate;
    }
}
