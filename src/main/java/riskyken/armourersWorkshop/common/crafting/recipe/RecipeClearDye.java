package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class RecipeClearDye implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        return getCraftingResult(invCrafting) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack skinStack = null;
        ItemStack soapStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item == ModItems.soap) {
                if (soapStack != null) {
                    return null;
                }
                soapStack = stack;
            } else if (item == ModItems.equipmentSkin) {
                if (skinStack != null) {
                    return null;
                }
                if (SkinNBTHelper.stackHasSkinData(stack)) {
                    skinStack = stack;
                } else {
                    return null;
                }
            }
        }
        
        if (skinStack != null && soapStack != null) {
            ItemStack returnStack = skinStack.copy();
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(returnStack);
            ISkinDye dye = skinPointer.getSkinDye();
            for (int i = 0; i < 8; i++) {
                dye.removeDye(i);
            }
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
