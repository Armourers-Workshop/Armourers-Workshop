package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

public final class ModBlockRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.colourable, 16, 0), new Object[] {
            "www",
            "wiw",
            "www",
            'w', Blocks.wool,
            'i', Items.iron_ingot});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.armourLibrary, 1, 0), new Object[] {
            "srs",
            "bcb",
            "sss",
            'r', new ItemStack(Blocks.wool, 1, 14),
            's', Blocks.stone,
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
            's', Blocks.stone});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.armourerBrain, 1, 0), new Object[] {
            "cwc",
            "wdw",
            "cwc",
            'w', new ItemStack(ModBlocks.colourable, 1, 0),
            'c', new ItemStack(ModBlocks.colourable, 1, 1),
            'd', Items.diamond});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.mannequin, 1, 0), new Object[] {
            " p ",
            "wcw",
            " w ",
            'w', "plankWood",
            'p', Blocks.pumpkin,
            'c', ModBlocks.colourable});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            new ItemStack(Blocks.redstone_lamp, 1)});
    }
}
