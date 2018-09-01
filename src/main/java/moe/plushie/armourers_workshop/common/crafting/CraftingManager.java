package moe.plushie.armourers_workshop.common.crafting;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.items.ModItems;
import net.minecraft.item.ItemStack;

public final class CraftingManager {

    
    
    public static void init() {
        /*
        GameRegistry.addRecipe(new RecipeSkinUpdate());
        GameRegistry.addRecipe(new RecipeSkinDye());
        GameRegistry.addRecipe(new RecipeClearDye());
        RecipeSorter.INSTANCE.register("armourersworkshop:shapeless", RecipeSkinUpdate.class, Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.INSTANCE.register("armourersworkshop:shapeless", RecipeSkinDye.class, Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.INSTANCE.register("armourersworkshop:shapeless", RecipeClearDye.class, Category.SHAPELESS, "after:minecraft:shapeless");
        
        hideItemsInNEI();
        if (!ConfigHandler.disableSkinningRecipes) {
            ItemSkinningRecipes.init();
        }
        if (!ConfigHandler.disableRecipes) {
            ModBlockRecipes.init();
            ModItemRecipes.init();
        }
        if (!ConfigHandler.disableDollRecipe) {
            new DollCraftingHandler();
        }*/
    }

    public static void addShapelessRecipe(ItemStack result, Object[] recipe) {
        //GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
    }

    public static void addShapedRecipe(ItemStack result, Object[] recipe) {
        //GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
    }
    
    public static void hideItemsInNEI() {
        if (ConfigHandler.hideDollFromCreativeTabs) {
            hideItemInNEI(new ItemStack(ModBlocks.doll, 1));
        }
        hideItemInNEI(new ItemStack(ModBlocks.boundingBox, 1));
        hideItemInNEI(new ItemStack(ModItems.armourContainer[0], 1));
        hideItemInNEI(new ItemStack(ModItems.armourContainer[1], 1));
        hideItemInNEI(new ItemStack(ModItems.armourContainer[2], 1));
        hideItemInNEI(new ItemStack(ModItems.armourContainer[3], 1));
        hideItemInNEI(new ItemStack(ModItems.equipmentSkin, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnable, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableGlowing, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableChild, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableChildGlowing, 1));
    }
    private static void hideItemInNEI(ItemStack stack) {
        ModAddonManager.addonNEI.hideItem(stack);
    }
}
