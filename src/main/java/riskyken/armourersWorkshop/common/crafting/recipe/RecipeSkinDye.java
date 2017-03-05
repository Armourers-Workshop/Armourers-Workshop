package riskyken.armourersWorkshop.common.crafting.recipe;

import java.util.Arrays;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;

public class RecipeSkinDye implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        return getCraftingResult(invCrafting) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack skinStack = null;
        ItemStack dyeStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                if (item == ModItems.dyeBottle) {
                    if (dyeStack != null) {
                        return null;
                    }
                    if (PaintingHelper.getToolHasPaint(stack)) {
                        dyeStack = stack;
                    } else{
                        return null;
                    }
                } else if (item == ModItems.equipmentSkin) {
                    if (skinStack != null) {
                        return null;
                    }
                    SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
                    if (skinPointer != null) {
                        if (skinPointer.getSkinDye().getNumberOfDyes() < SkinDye.MAX_SKIN_DYES) {
                            skinStack = stack;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        
        if (skinStack != null && dyeStack != null) {
            ItemStack returnStack = skinStack.copy();
            byte[] dyeColour = PaintingHelper.getToolPaintColourArray(dyeStack);
            ModLogger.log("dye size: " + dyeColour.length);
            ModLogger.log(Arrays.toString(dyeColour));
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(returnStack);
            ISkinDye dye = skinPointer.getSkinDye();
            dye.addDye(dyeColour);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
            return returnStack;
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
