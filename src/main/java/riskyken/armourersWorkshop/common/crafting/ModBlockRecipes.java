package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;

public final class ModBlockRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.colourable, 16, 0), new Object[] {
            "www",
            "wiw",
            "www",
            'w', Blocks.wool,
            'i', "ingotIron"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.armourLibrary, 1, 0), new Object[] {
            "srs",
            "bcb",
            "sss",
            'r', new ItemStack(Blocks.wool, 1, 14),
            's', "stone",
            'c', ModBlocks.colourable,
            'b', Items.book});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.colourMixer, 1, 0), new Object[] {
            "rgb",
            "scs",
            "sss",
            'r', "dyeRed",
            'g', "dyeGreen",
            'b', "dyeBlue",
            'c', ModBlocks.colourable,
            's', "stone"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.armourerBrain, 1, 0), new Object[] {
            "ses",
            "dcd",
            "sss",
            's', "stone",
            'c', ModBlocks.colourable,
            'e', Items.ender_pearl,
            'd', "gemDiamond"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.mannequin, 1, 0), new Object[] {
            " p ",
            "wcw",
            " w ",
            'w', "plankWood",
            'p', Blocks.pumpkin,
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.skinningTable, 1, 0), new Object[] {
            "srs",
            "tct",
            "sss",
            'r', new ItemStack(Blocks.wool, 1, 14),
            's', "stone",
            'c', ModBlocks.colourable,
            't', ModItems.equipmentSkinTemplate});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.dyeTable, 1, 0), new Object[] {
            "srs",
            "dcd",
            "sss",
            'r', new ItemStack(Blocks.wool, 1, 14),
            's', "stone",
            'c', ModBlocks.colourable,
            'd', ModItems.dyeBottle});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            new ItemStack(Items.glowstone_dust, 1)});
        /*
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.miniArmourer, 1, 0), new Object[] {
            new ItemStack(ModBlocks.armourerBrain, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.armourerBrain, 1, 0), new Object[] {
            new ItemStack(ModBlocks.miniArmourer, 1)});
        */
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlass, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            "blockGlass"});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlassGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlowing, 1),
            "blockGlass"});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourable, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlass, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourableGlassGlowing, 1)});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlassGlowing, 1, 0), new Object[] {
                new ItemStack(ModBlocks.colourableGlass, 1),
                new ItemStack(Items.glowstone_dust, 1)});
    }
}
