package riskyken.armourersWorkshop.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ModItems;

public final class ModItemRecipes {

    public static void init() {

        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintbrush, 1, 0), new Object[] {
            "  w",
            " i ",
            "s  ",
            'w', Blocks.wool,
            'i', Items.iron_ingot,
            's', "stickWood"});
        
        CraftingManager.addShapedRecipe(new ItemStack(ModItems.paintRoller, 1, 0), new Object[] {
            " w ",
            " iw",
            "s  ",
            'w', Blocks.wool,
            'i', Items.iron_ingot,
            's', "stickWood"});
    }
}
