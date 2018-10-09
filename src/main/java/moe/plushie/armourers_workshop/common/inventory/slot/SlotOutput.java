package moe.plushie.armourers_workshop.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotOutput extends SlotHidable {

    private final Container callback;
    
    public SlotOutput(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        this(inventory, slotIndex, xDisplayPosition, yDisplayPosition, null);
    }
    
    public SlotOutput(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition, Container callback) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.callback = callback;
    }
    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        if (callback != null) {
            callback.onCraftMatrixChanged(inventory);
        }
        return super.onTake(thePlayer, stack);
    }
    
    /*
    @Override
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
        if (callback != null) {
            callback.onCraftMatrixChanged(inventory);
        }
    }*/
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }
    
}
