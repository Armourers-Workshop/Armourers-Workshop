package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;

public class SlotEquipmentSkinTemplate extends Slot {
    
    private final ISlotChanged callback;
    
    public SlotEquipmentSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition, ISlotChanged callback) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.callback = callback;
    }
    
    public SlotEquipmentSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, null);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkinTemplate && stack.getItemDamage() == 0) {
            return true;
        }
        if (stack.getItem() instanceof ItemEquipmentSkin) {
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
