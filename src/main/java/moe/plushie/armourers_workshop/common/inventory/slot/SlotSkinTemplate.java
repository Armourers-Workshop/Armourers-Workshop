package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.common.init.items.ItemArmourContainerItem;
import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import moe.plushie.armourers_workshop.common.init.items.ItemSkinTemplate;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotSkinTemplate extends SlotHidable {
    
    private final ISlotChanged callback;
    
    public SlotSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition, ISlotChanged callback) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.callback = callback;
    }
    
    public SlotSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, null);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemSkinTemplate) {
            return true;
        }
        if (stack.getItem() instanceof ItemSkin) {
            return true;
        }
        if (stack.getItem() instanceof ItemArmourContainerItem) {
            return true;
        }
        return false;
    }
    
    @Override
    public void onSlotChanged() {
        if (callback != null) {
            callback.onSlotChanged(getSlotIndex());
        }
        super.onSlotChanged();
    }
}
