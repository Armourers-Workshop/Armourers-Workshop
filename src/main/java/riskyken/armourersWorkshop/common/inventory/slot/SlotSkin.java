package riskyken.armourersWorkshop.common.inventory.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class SlotSkin extends SlotHidable {
    
    private ISkinType skinType;
    
    public SlotSkin(ISkinType skinType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.skinType = skinType;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemSkin) {
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
