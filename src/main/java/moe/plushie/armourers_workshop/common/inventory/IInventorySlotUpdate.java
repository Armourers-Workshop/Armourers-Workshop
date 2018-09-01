package moe.plushie.armourers_workshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventorySlotUpdate {

    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack);
}
