package moe.plushie.armourers_workshop.common.crafting;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.handler.DollCraftingHandler;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
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
        */
        if (!ConfigHandler.disableSkinningRecipes) {
            ItemSkinningRecipes.init();
        }
        new DollCraftingHandler();
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        if (!ConfigHandler.disableRecipes) {
            ModBlockRecipes.init(event.getRegistry());
            ModItemRecipes.init(event.getRegistry());
        }
    }

    public static void addShapelessRecipe(IForgeRegistry<IRecipe> iForgeRegistry, ItemStack result, Object[] recipe, ResourceLocation registryName) {
        iForgeRegistry.register(new ShapelessOreRecipe(null, result, recipe).setRegistryName(registryName));
    }

    public static void addShapelessRecipe(IForgeRegistry<IRecipe> iForgeRegistry, ItemStack result, Object[] recipe) {
        iForgeRegistry.register(new ShapelessOreRecipe(null, result, recipe).setRegistryName(result.getItem().getRegistryName()));
    }

    public static void addShapedRecipe(IForgeRegistry<IRecipe> iForgeRegistry, ItemStack result, Object[] recipe, ResourceLocation registryName) {
        iForgeRegistry.register(new ShapedOreRecipe(null, result, recipe).setRegistryName(registryName));
    }

    public static void addShapedRecipe(IForgeRegistry<IRecipe> iForgeRegistry, ItemStack result, Object[] recipe) {
        iForgeRegistry.register(new ShapedOreRecipe(null, result, recipe).setRegistryName(result.getItem().getRegistryName()));
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
        hideItemInNEI(new ItemStack(ModItems.skin, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnable, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableGlowing, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableChild, 1));
        hideItemInNEI(new ItemStack(ModBlocks.skinnableChildGlowing, 1));
    }

    private static void hideItemInNEI(ItemStack stack) {
        ModAddonManager.addonNEI.hideItem(stack);
    }
}
