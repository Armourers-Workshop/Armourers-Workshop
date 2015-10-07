package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;

public class SlotEquipmentSkinTemplate extends Slot {
    
    private boolean allowTemplates;
    
    public SlotEquipmentSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition, boolean allowTemplates) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.allowTemplates = allowTemplates;
    }
    
    public SlotEquipmentSkinTemplate(IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, true);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (allowTemplates & stack.getItem() instanceof ItemEquipmentSkinTemplate && stack.getItemDamage() == 0) {
            return true;
        }
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            return true;
        }
        return false;
    }
}
