package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;

public class SlotEquipmentSkin extends SlotHidable {
    
    private IEquipmentSkinType skinType;
    
    public SlotEquipmentSkin(IEquipmentSkinType skinType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.skinType = skinType;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            if (EquipmentNBTHelper.stackHasSkinData(stack)) {
                SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(stack);
                if (this.skinType != null && this.skinType == skinData.skinType) {
                    return true;
                }
            }
        }
        return false;
    }
}
