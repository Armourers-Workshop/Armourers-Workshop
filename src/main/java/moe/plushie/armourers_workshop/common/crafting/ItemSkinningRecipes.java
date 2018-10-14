package moe.plushie.armourers_workshop.common.crafting;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeItemSkinning;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinArmour;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinArmourContainer;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinClear;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinCopy;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinItem;
import moe.plushie.armourers_workshop.common.crafting.recipe.RecipeSkinRecover;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemSkinningRecipes {
    
    public static ArrayList<RecipeItemSkinning> recipes = new ArrayList<RecipeItemSkinning>();
    
    public static void init() {
        
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinSword, ItemOverrideType.SWORD));
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinShield, ItemOverrideType.SHIELD));
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinBow, ItemOverrideType.BOW));
        
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinPickaxe, ItemOverrideType.PICKAXE));
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinAxe, ItemOverrideType.AXE));
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinShovel, ItemOverrideType.SHOVEL));
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinHoe, ItemOverrideType.HOE));
        
        recipes.add(new RecipeSkinItem(SkinTypeRegistry.skinItem, ItemOverrideType.ITEM));
        
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
    }
    
    public static ItemStack getRecipeOutput(IInventory inventory) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).matches(inventory)) {
                return recipes.get(i).getCraftingResult(inventory);
            }
        }
        return ItemStack.EMPTY;
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
