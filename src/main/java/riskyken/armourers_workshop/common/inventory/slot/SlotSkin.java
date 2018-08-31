package riskyken.armourers_workshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.common.items.ItemSkin;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

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
                if (this.skinType != null && this.skinType == skinData.getIdentifier().getSkinType()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getBackgroundIconIndex() {
        if (skinType == SkinTypeRegistry.skinSword & getSlotIndex() > 0) {
            if (getSlotIndex() == 1) {
                return ClientProxy.iconSkinPickaxe;
            } else if (getSlotIndex() == 2) {
                return ClientProxy.iconSkinAxe;
            } else if (getSlotIndex() == 3) {
                return ClientProxy.iconSkinShovel;
            } else if (getSlotIndex() == 4) {
                return ClientProxy.iconSkinHoe;
            }
        }
        if (this.skinType != null) {
            return this.skinType.getEmptySlotIcon();
        }
        return super.getBackgroundIconIndex();
    }*/
}
