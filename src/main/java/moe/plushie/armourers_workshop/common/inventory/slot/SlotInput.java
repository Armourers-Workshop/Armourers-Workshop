package moe.plushie.armourers_workshop.common.inventory.slot;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotInput extends Slot {
    
    private final Container callback;
    
    public SlotInput(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        this(inventory, slotIndex, xDisplayPosition, yDisplayPosition, null);
    }
    
    public SlotInput(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition, Container callback) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.callback = callback;
    }
    
    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        if (callback != null) {
            //callback.onCraftMatrixChanged(inventory);
            callback.detectAndSendChanges();
        }
    }
}
