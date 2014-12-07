package riskyken.armourersWorkshop.common.crafting;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public final class CraftingManager {

    public static void init() {
        if (ConfigHandler.disableRecipes) { return; }
        ModBlockRecipes.init();
        ModItemRecipes.init();
        hideItemsInNEI();
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
                ItemStack dollStack = new ItemStack(ModBlocks.doll, 1);
                ccHideStack.invoke(null, dollStack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
