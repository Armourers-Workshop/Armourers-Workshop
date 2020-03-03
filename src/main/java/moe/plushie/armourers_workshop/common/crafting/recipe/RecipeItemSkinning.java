package moe.plushie.armourers_workshop.common.crafting.recipe;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;


public abstract class RecipeItemSkinning {
    
    protected ISkinType skinType;
    
    public RecipeItemSkinning(ISkinType skinType) {
        this.skinType = skinType;
    }
    
    public abstract boolean matches(IInventory inventory);
    
    public abstract ItemStack getCraftingResult(IInventory inventory);
    
    public abstract void onCraft(IInventory inventory);
    
    protected boolean isValidSkinForType(ItemStack stack) {
        return stack.getItem() == ModItems.SKIN &&
                SkinNBTHelper.stackHasSkinData(stack) &&
                SkinNBTHelper.getSkinTypeFromStack(stack) == skinType;
    }
}
