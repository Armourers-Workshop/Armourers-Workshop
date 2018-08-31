package riskyken.armourers_workshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.common.items.ModItems;
import riskyken.armourers_workshop.utils.SkinNBTHelper;


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
                SkinNBTHelper.stackHasSkinData(stack) &&
                SkinNBTHelper.getSkinTypeFromStack(stack) == skinType;
    }
}
