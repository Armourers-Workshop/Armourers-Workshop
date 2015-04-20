package riskyken.armourersWorkshop.common.crafting;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeSkinUpdate;
import riskyken.armourersWorkshop.common.crafting.recipe.RecipeSwordSkin;
import riskyken.armourersWorkshop.common.handler.DollCraftinghandler;
import riskyken.armourersWorkshop.common.items.ModItems;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public final class CraftingManager {

    public static void init() {
        hideItemsInNEI();
        if (ConfigHandler.disableRecipes) { return; }
        ModBlockRecipes.init();
        ModItemRecipes.init();
        new DollCraftinghandler();
        GameRegistry.addRecipe(new RecipeSwordSkin());
        GameRegistry.addRecipe(new RecipeSkinUpdate());
    }

    public static void addShapelessRecipe(ItemStack result, Object[] recipe) {
        GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
    }

    public static void addShapedRecipe(ItemStack result, Object[] recipe) {
        GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
    }
    
    public static void hideItemsInNEI() {
        if (Loader.isModLoaded("NotEnoughItems")) {
            try {
                Class ccApi = Class.forName("codechicken.nei.api.API");
                Method ccHideStack = ccApi.getMethod("hideItem", ItemStack.class);
                ccHideStack.invoke(null, new ItemStack(ModBlocks.doll, 1));
                ccHideStack.invoke(null, new ItemStack(ModBlocks.boundingBox, 1));
                ccHideStack.invoke(null, new ItemStack(ModItems.armourContainer[0], 1));
                ccHideStack.invoke(null, new ItemStack(ModItems.armourContainer[1], 1));
                ccHideStack.invoke(null, new ItemStack(ModItems.armourContainer[2], 1));
                ccHideStack.invoke(null, new ItemStack(ModItems.armourContainer[3], 1));
                /*
                for (int i = 0; i < SkinTypeRegistry.INSTANCE.getNumberOfSkinRegistered(); i++) {
                    ccHideStack.invoke(null, new ItemStack(ModItems.equipmentSkin, 1, i));
                }
                */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
