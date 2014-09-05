package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

public final class ModBlockRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModBlocks.colourable, 8, 0), new Object[] {
            "www",
            "wiw",
            "www",
            'w', Blocks.wool,
            'i', Items.iron_ingot});
        
        CraftingManager.addShapelessRecipe(new ItemStack(ModBlocks.colourableGlowing, 1, 0), new Object[] {
            new ItemStack(ModBlocks.colourable, 1),
            new ItemStack(Blocks.redstone_lamp, 1)});
    }
}
