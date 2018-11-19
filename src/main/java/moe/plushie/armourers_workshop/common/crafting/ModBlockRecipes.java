package moe.plushie.armourers_workshop.common.crafting;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlockRecipes {

    public static void init(IForgeRegistry<IRecipe> iForgeRegistry) {
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCube, 1, 0), new Object[] {
                new ItemStack(ModBlocks.skinCubeGlass, 1, 0)}, new ResourceLocation(LibModInfo.ID, "recipe." + ModBlocks.skinCube.getTranslationKey() + ".2"));
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCubeGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.skinCube, 1),
            new ItemStack(Items.GLOWSTONE_DUST, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCubeGlass, 1, 0), new Object[] {
            new ItemStack(ModBlocks.skinCube, 1),
            "blockGlass"});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCubeGlassGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.skinCubeGlowing, 1),
            "blockGlass"});
        

        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCubeGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.skinCubeGlassGlowing, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCubeGlassGlowing, 1, 0), new Object[] {
                new ItemStack(ModBlocks.skinCubeGlass, 1),
                new ItemStack(Items.GLOWSTONE_DUST, 1)});
        
    }
}
