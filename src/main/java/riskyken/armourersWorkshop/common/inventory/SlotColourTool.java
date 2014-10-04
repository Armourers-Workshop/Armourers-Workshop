package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;

public class SlotColourTool extends Slot {
    
    public SlotColourTool(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof IPaintingTool;
    }
}
