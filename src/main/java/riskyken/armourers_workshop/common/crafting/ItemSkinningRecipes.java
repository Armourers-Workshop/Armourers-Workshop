package riskyken.armourers_workshop.common.crafting;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.crafting.recipe.RecipeItemSkinning;

public class ItemSkinningRecipes {
    
    public static ArrayList<RecipeItemSkinning> recipes = new ArrayList<RecipeItemSkinning>();
    private static ArrayList<Item> skinnableItems = new ArrayList<Item>();
    
    public static void init() {
        /*
        recipes.add(new RecipeSkinSword());
        recipes.add(new RecipeSkinBow());
        recipes.add(new RecipeSkinPickaxe());
        recipes.add(new RecipeSkinCopy());
        recipes.add(new RecipeSkinClear());
        recipes.add(new RecipeSkinRecover());
        recipes.add(new RecipeSkinArmour(SkinTypeRegistry.skinHead));
        recipes.add(new RecipeSkinArmour(SkinTypeRegistry.skinChest));
        recipes.add(new RecipeSkinArmour(SkinTypeRegistry.skinLegs));
        recipes.add(new RecipeSkinArmour(SkinTypeRegistry.skinFeet));
        recipes.add(new RecipeSkinArmourContainer(SkinTypeRegistry.skinHead));
        recipes.add(new RecipeSkinArmourContainer(SkinTypeRegistry.skinChest));
        recipes.add(new RecipeSkinArmourContainer(SkinTypeRegistry.skinLegs));
        recipes.add(new RecipeSkinArmourContainer(SkinTypeRegistry.skinFeet));
        */
    }
    
    public static void addSkinnableItem(Item item) {
        skinnableItems.add(item);
    }
    
    public static boolean isItemSkinable(Item item) {
        for (int i = 0; i < skinnableItems.size(); i++) {
            if (item == skinnableItems.get(i)) {
                return true;
            }
        }
        return false;
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
