package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class SkinningRecipes {

    public static ArrayList<SkinningRecipe> recipes = new ArrayList<>();

    public static void init() {

        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_SWORD));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_SHIELD));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_BOW));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_TRIDENT));

        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_PICKAXE));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_AXE));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_SHOVEL));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_HOE));

        recipes.add(new SkinningItemRecipe(SkinTypes.BOAT));
        recipes.add(new SkinningItemRecipe(SkinTypes.MINECART));
        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM_FISHING));

        recipes.add(new SkinningItemRecipe(SkinTypes.ITEM));

        recipes.add(new SkinningCopyRecipe());
        recipes.add(new SkinningClearRecipe());
//        recipes.add(new RecipeSkinRecover());

        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_HEAD));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_CHEST));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_LEGS));
        recipes.add(new SkinningArmourRecipe(SkinTypes.ARMOR_FEET));

        recipes.add(new SkinningHorseArmorRecipe(SkinTypes.HORSE));
    }

    public static ItemStack getRecipeOutput(Container inventory, SkinOptions options) {
        for (var recipe : recipes) {
            var itemStack = recipe.test(inventory, options);
            if (!itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onCraft(Container inventory, SkinOptions options) {
        for (var recipe : recipes) {
            var itemStack = recipe.test(inventory, options);
            if (!itemStack.isEmpty()) {
                recipe.apply(inventory);
                return;
            }
        }
    }
}
