package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;

public final class ModItemRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintbrush, 1, 0), new Object[] {
            "  w",
            " i ",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintRoller, 1, 0), new Object[] {
            " w ",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.colourPicker, 1, 0), new Object[] {
            " lg",
            "lwl",
            "ll ",
            'w', ModBlocks.colourable,
            'g', Blocks.glass,
            'l', Items.leather});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.burnTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', "dyeBlack",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.dodgeTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', "dyeWhite",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.shadeNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', Blocks.cobblestone,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.hueTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', "dyeGray",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.colourNoiseTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', Blocks.mossy_cobblestone,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.mannequinTool, 1, 0), new Object[] {
            " wd",
            " iw",
            "s  ",
            'w', ModBlocks.colourable,
            'i', Items.iron_ingot,
            'd', "plankWood",
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.equipmentSkinTemplate, 8, 0), new Object[] {
            "cc",
            "cc",
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.guideBook, 1, 0), new Object[] {
            new ItemStack(Items.book, 1),
            new ItemStack(ModBlocks.colourable, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModItems.soap, 1, 0), new Object[] {
            new ItemStack(Items.water_bucket, 1),
            new ItemStack(Items.rotten_flesh, 1),
            new ItemStack(Items.slime_ball, 1)});
    }
}
