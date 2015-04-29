package riskyken.armourersWorkshop.common.crafting;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeItemSkinning;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeSkinClear;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeSkinCopy;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeSkinSword;

public class ItemSkinningRecipes {
    
    public static ArrayList<RecipeItemSkinning> recipes = new ArrayList<RecipeItemSkinning>();
    
    public static void init() {
        recipes.add(new RecipeSkinSword());
        recipes.add(new RecipeSkinCopy());
        recipes.add(new RecipeSkinClear());
    }
    
    public static ItemStack getRecipeOutput(IInventory inventory) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).matches(inventory)) {
                return recipes.get(i).getCraftingResult(inventory);
            }
        }
        return null;
    }
    
    public static void onCraft(IInventory inventory) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).matches(inventory)) {
                recipes.get(i).onCraft(inventory);
                return;
            }
        }
    }
}
