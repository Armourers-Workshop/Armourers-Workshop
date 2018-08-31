package riskyken.armourers_workshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.items.ModItems;
import riskyken.armourers_workshop.common.skin.data.SkinDye;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class RecipeSkinCopy extends RecipeItemSkinning {

    public RecipeSkinCopy() {
        super(null);
    }

    @Override
    public boolean matches(IInventory inventory) {
        return getCraftingResult(inventory) != null;
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = null;
        ItemStack blackStack = null;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (item == ModItems.equipmentSkin && SkinNBTHelper.stackHasSkinData(stack)) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (item == ModItems.equipmentSkinTemplate & !SkinNBTHelper.stackHasSkinData(stack)) {
                    if (blackStack != null) {
                        return null;
                    }
                    blackStack = stack;
                } else {
                    return null;
                }
                
            }
        }
        
        if (skinStack != null && blackStack != null) {
            ItemStack returnStack = new ItemStack(ModItems.equipmentSkin, 1);
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(skinStack);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), false, new SkinDye(skinData.getSkinDye()));
            return returnStack;
        }
        return null;
    }
    
    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            Item item = stack.getItem();
            if (item == ModItems.equipmentSkinTemplate & !SkinNBTHelper.stackHasSkinData(stack)) {
                inventory.decrStackSize(slotId, 1);
            }
        }
    }
}
