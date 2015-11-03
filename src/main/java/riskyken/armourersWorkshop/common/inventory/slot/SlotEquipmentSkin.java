package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotEquipmentSkin extends SlotHidable {
    
    private ISkinType skinType;
    
    public SlotEquipmentSkin(ISkinType skinType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.skinType = skinType;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            if (SkinNBTHelper.stackHasSkinData(stack)) {
                SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
                if (this.skinType != null && this.skinType == skinData.skinType) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getBackgroundIconIndex() {
        if (this.skinType != null) {
            return this.skinType.getEmptySlotIcon();
        }
        return super.getBackgroundIconIndex();
    }
}
