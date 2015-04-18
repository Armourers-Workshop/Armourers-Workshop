package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;

public class SlotEquipmentSkin extends SlotHidable {
    
    private ISkinType skinType;
    
    public SlotEquipmentSkin(ISkinType skinType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.skinType = skinType;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            if (this.skinType != null && stack.getItemDamage() == this.skinType.getId()) {
                return true;
            }
        }
        return false;
    }
}
