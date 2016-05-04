package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.common.items.ItemSkinTemplate;

public class SlotSkinTemplate extends Slot {
    
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
        if (stack.getItem() instanceof ItemSkinTemplate && stack.getItemDamage() == 0) {
            return true;
        }
        if (stack.getItem() instanceof ItemSkin) {
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
