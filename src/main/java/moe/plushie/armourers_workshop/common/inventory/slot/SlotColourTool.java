package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotColourTool extends SlotHidable {
    
    public SlotColourTool(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof IPaintingTool;
    }
}
