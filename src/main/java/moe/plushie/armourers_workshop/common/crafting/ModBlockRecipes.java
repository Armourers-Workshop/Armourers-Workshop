package moe.plushie.armourers_workshop.common.crafting;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlockRecipes {

    public static void init(IForgeRegistry<IRecipe> iForgeRegistry) {

        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinCube, 16, 0), new Object[] {
            "www",
            "wiw",
            "www",
            'w',  new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
            'i', "ingotIron"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinLibrary, 1, 0), new Object[] {
            "srs",
            "bcb",
            "sss",
            'r', new ItemStack(Blocks.WOOL, 1, 14),
            's', "stone",
            'c', ModBlocks.skinCube,
            'b', Items.BOOK});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.globalSkinLibrary, 1, 0), new Object[] {
            "srs",
            "bcb",
            "sss",
            'r', "enderpearl",
            's', "stone",
            'c', ModBlocks.skinCube,
            'b', Items.BOOK});
        /*
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.hologramProjector, 1, 0), new Object[] {
            "igi",
            "ici",
            "iii",
            'i', Items.IRON_INGOT,
            'g', "blockGlassLightBlue",
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourMixer, 1, 0), new Object[] {
            "rgb",
            "scs",
            "sss",
            'r', "dyeRed",
            'g', "dyeGreen",
            'b', "dyeBlue",
            'c', ModBlocks.colourable,
            's', "stone"});
        */
        /*
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.armourerBrain, 1, 0), new Object[] {
            "ses",
            "dcd",
            "sss",
            's', "stone",
            'c', ModBlocks.colourable,
            'e', Items.ender_pearl,
            'd', "gemDiamond"});
        */
        /*
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.mannequin, 1, 0), new Object[] {
            " p ",
            "wcw",
            " w ",
            'w', "plankWood",
            'p', Blocks.PUMPKIN,
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.skinningTable, 1, 0), new Object[] {
            "srs",
            "tct",
            "sss",
            'r', new ItemStack(Blocks.WOOL, 1, 14),
            's', "stone",
            'c', ModBlocks.colourable,
            't', ModItems.equipmentSkinTemplate});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModBlocks.dyeTable, 1, 0), new Object[] {
            "srs",
            "dcd",
            "sss",
            'r', "plankWood",
            's', "stone",
            'c', ModBlocks.colourable,
            'd', ModItems.dyeBottle});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            new ItemStack(Items.GLOWSTONE_DUST, 1)});
        */
        /*
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.miniArmourer, 1, 0), new Object[] {
            new ItemStack(ModBlocks.armourerBrain, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.armourerBrain, 1, 0), new Object[] {
            new ItemStack(ModBlocks.miniArmourer, 1)});
        */
        /*
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourableGlass, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            "blockGlass"});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourableGlassGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlowing, 1),
            "blockGlass"});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourable, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlass, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlassGlowing, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModBlocks.colourableGlassGlowing, 1, 0), new Object[] {
                new ItemStack(ModBlocks.colourableGlass, 1),
                new ItemStack(Items.GLOWSTONE_DUST, 1)});
        */
    }
}
