package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.item.ItemStack;

public interface IInventorySlotUpdate {

    public void setInventorySlotContents(int slotId, ItemStack stack);
}
