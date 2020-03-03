package moe.plushie.armourers_workshop.common.crafting;

import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModItemRecipes {

    public static void init(IForgeRegistry<IRecipe> iForgeRegistry) {
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.blendingTool, 1, 0), new Object[] {
            " wc",
            " ib",
            "s  ",
            'c', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'w', "dyeWhite",
            'b', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.blendingTool, 1, 0), new Object[] {
            " bc",
            " iw",
            "s  ",
            'c', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'w', "dyeWhite",
            'b', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.blockMarker, 1, 0), new Object[] {
            "  b",
            " c ",
            "b  ",
            'c', ModBlocks.SKIN_CUBE,
            'b', "dyeBlack"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.shadeNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'd', "cobblestone",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.hueTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'd', "dyeGray",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.colourNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'd', Blocks.MOSSY_COBBLESTONE,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.mannequinTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.SKIN_CUBE,
            'i', "ingotIron",
            'd', "plankWood",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.skinTemplate, 8, 0), new Object[] {
            "cc",
            "cc",
            'c', ModBlocks.SKIN_CUBE});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.dyeBottle, 1, 0), new Object[] {
                "gcg",
                "g g",
                "ggg",
                'c', ModBlocks.SKIN_CUBE,
                'g', "paneGlass"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.armourersHammer, 1, 0), new Object[] {
                " iw",
                " ii",
                "s  ",
                'w', ModBlocks.SKIN_CUBE,
                'i', "ingotIron",
                's', "stickWood"});
        
        CraftingManager.addShapedRecipe(iForgeRegistry, new ItemStack(ModItems.linkingTool, 1, 0), new Object[] {
                " iw",
                " ci",
                "s  ",
                'w', ModBlocks.SKIN_CUBE,
                'i', "ingotIron",
                'c', Blocks.CHEST,
                's', "stickWood"});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.guideBook, 1, 0), new Object[] {
            new ItemStack(Items.BOOK, 1),
            new ItemStack(ModBlocks.SKIN_CUBE, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.soap, 1, 0), new Object[] {
            new ItemStack(Items.WATER_BUCKET, 1),
            new ItemStack(Items.ROTTEN_FLESH, 1),
            "slimeball"});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.skinUnlock, 1, 0), new Object[] {
                new ItemStack(Items.DIAMOND_HELMET, 1),
                new ItemStack(ModItems.skinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.skinUnlock, 1, 1), new Object[] {
                new ItemStack(Items.DIAMOND_CHESTPLATE, 1),
                new ItemStack(ModItems.skinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.skinUnlock, 1, 2), new Object[] {
                new ItemStack(Items.DIAMOND_LEGGINGS, 1),
                new ItemStack(ModItems.skinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.skinUnlock, 1, 3), new Object[] {
                new ItemStack(Items.DIAMOND_BOOTS, 1),
                new ItemStack(ModItems.skinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(iForgeRegistry, new ItemStack(ModItems.skinUnlock, 1, 4), new Object[] {
                new ItemStack(Items.FEATHER, 1),
                new ItemStack(ModItems.skinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
    }
}
