package riskyken.armourers_workshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.items.ModItems;

public final class ModItemRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintbrush, 1, 0), new Object[] {
            "  w",
            " i ",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintRoller, 1, 0), new Object[] {
            " w ",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.colourPicker, 1, 0), new Object[] {
            " lg",
            "lwl",
            "ll ",
            'w', ModBlocks.colourable,
            'g', "blockGlass",
            'l', Items.LEATHER});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.burnTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.dodgeTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', "dyeWhite",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.blendingTool, 1, 0), new Object[] {
            " wc",
            " ib",
            "s  ",
            'c', ModBlocks.colourable,
            'i', "ingotIron",
            'w', "dyeWhite",
            'b', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.blendingTool, 1, 0), new Object[] {
            " bc",
            " iw",
            "s  ",
            'c', ModBlocks.colourable,
            'i', "ingotIron",
            'w', "dyeWhite",
            'b', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.blockMarker, 1, 0), new Object[] {
            "  b",
            " c ",
            "b  ",
            'c', ModBlocks.colourable,
            'b', "dyeBlack"});
        
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.shadeNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', "cobblestone",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.hueTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', "dyeGray",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.colourNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', Blocks.MOSSY_COBBLESTONE,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.mannequinTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', "ingotIron",
            'd', "plankWood",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.equipmentSkinTemplate, 8, 0), new Object[] {
            "cc",
            "cc",
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.dyeBottle, 1, 0), new Object[] {
                "gcg",
                "g g",
                "ggg",
                'c', ModBlocks.colourable,
                'g', "paneGlass"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.armourersHammer, 1, 0), new Object[] {
                " iw",
                " ii",
                "s  ",
                'w', ModBlocks.colourable,
                'i', "ingotIron",
                's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.linkingTool, 1, 0), new Object[] {
                " iw",
                " ci",
                "s  ",
                'w', ModBlocks.colourable,
                'i', "ingotIron",
                'c', Blocks.CHEST,
                's', "stickWood"});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.guideBook, 1, 0), new Object[] {
            new ItemStack(Items.BOOK, 1),
            new ItemStack(ModBlocks.colourable, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.soap, 1, 0), new Object[] {
            new ItemStack(Items.WATER_BUCKET, 1),
            new ItemStack(Items.ROTTEN_FLESH, 1),
            "slimeball"});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 0), new Object[] {
                new ItemStack(Items.DIAMOND_HELMET, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 1), new Object[] {
                new ItemStack(Items.DIAMOND_CHESTPLATE, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 2), new Object[] {
                new ItemStack(Items.DIAMOND_LEGGINGS, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 3), new Object[] {
                new ItemStack(Items.DIAMOND_BOOTS, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 4), new Object[] {
                new ItemStack(Items.FEATHER, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.NETHER_STAR, 1)});
    }
}
