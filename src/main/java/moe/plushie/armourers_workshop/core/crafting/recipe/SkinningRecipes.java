package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class SkinningRecipes {

    public static ArrayList<SkinningRecipe> recipes = new ArrayList<>();

    public static void init() {

        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_SWORD));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_SHIELD));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_BOW));

        recipes.add(new SkinningItemRecipe(SkinTypes.TOOL_PICKAXE));
        recipes.add(new SkinningItemRecipe(SkinTypes.TOOL_AXE));
        recipes.add(new SkinningItemRecipe(SkinTypes.TOOL_SHOVEL));
        recipes.add(new SkinningItemRecipe(SkinTypes.TOOL_HOE));

        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM));

        recipes.add(new SkinningCopyRecipe());
        recipes.add(new SkinningClearRecipe());
//        recipes.add(new RecipeSkinRecover());

        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_HEAD));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_CHEST));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_LEGS));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_FEET));

        recipes.add(new SkinningContainerRecipe(SkinTypes.ARMOR_HEAD));
        recipes.add(new SkinningContainerRecipe(SkinTypes.ARMOR_CHEST));
        recipes.add(new SkinningContainerRecipe(SkinTypes.ARMOR_LEGS));
        recipes.add(new SkinningContainerRecipe(SkinTypes.ARMOR_FEET));
    }

    public static ItemStack getRecipeOutput(IInventory inventory) {
        for (SkinningRecipe recipe : recipes) {
            ItemStack itemStack = recipe.test(inventory);
            if (!itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onCraft(IInventory inventory) {
        for (SkinningRecipe recipe : recipes) {
            ItemStack itemStack = recipe.test(inventory);
            if (!itemStack.isEmpty()) {
                recipe.apply(inventory);
                return;
            }
        }
    }
}
