package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;


public abstract class RecipeItemSkinning {
    
    protected ISkinType skinType;
    
    public RecipeItemSkinning(ISkinType skinType) {
        this.skinType = skinType;
    }
    
    public abstract boolean matches(IInventory inventory);
    
    public abstract ItemStack getCraftingResult(IInventory inventory);
    
    public abstract void onCraft(IInventory inventory);
    
    protected boolean isValidSkinForType(ItemStack stack) {
        return stack.getItem() == ModItems.equipmentSkin &&
                EquipmentNBTHelper.stackHasSkinData(stack) &&
                EquipmentNBTHelper.getSkinTypeFromStack(stack) == skinType;
    }
}
