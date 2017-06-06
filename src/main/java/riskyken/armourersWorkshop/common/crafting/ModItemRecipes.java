package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.plushieWrapper.common.registry.ModRegistry;

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
            'l', Items.leather});
        
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
        
        CraftingManager.addShapedRecipe(new ItemStack(ModRegistry.getMinecraftItem(ModItems.blockMarker), 1, 0), new Object[] {
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
            'd', Blocks.mossy_cobblestone,
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
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModRegistry.getMinecraftItem(ModItems.guideBook), 1, 0), new Object[] {
            new ItemStack(Items.book, 1),
            new ItemStack(ModBlocks.colourable, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModRegistry.getMinecraftItem(ModItems.soap), 1, 0), new Object[] {
            new ItemStack(Items.water_bucket, 1),
            new ItemStack(Items.rotten_flesh, 1),
            "slimeball"});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 0), new Object[] {
                new ItemStack(Items.diamond_helmet, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.nether_star, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 1), new Object[] {
                new ItemStack(Items.diamond_chestplate, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.nether_star, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 2), new Object[] {
                new ItemStack(Items.diamond_leggings, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.nether_star, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.skinUnlock, 1, 3), new Object[] {
                new ItemStack(Items.diamond_boots, 1),
                new ItemStack(ModItems.equipmentSkinTemplate, 1),
                new ItemStack(Items.nether_star, 1)});
    }
}
