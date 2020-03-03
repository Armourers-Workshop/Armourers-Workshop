package moe.plushie.armourers_workshop.common.crafting.recipe;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeSkinRecover extends RecipeItemSkinning {

    public RecipeSkinRecover() {
        super(null);
    }

    @Override
    public boolean matches(IInventory inventory) {
        return !getCraftingResult(inventory).isEmpty();
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        if (!ConfigHandler.enableRecoveringSkins) {
            return ItemStack.EMPTY;
        }
        ItemStack skinStack = ItemStack.EMPTY;
        ItemStack blackStack = ItemStack.EMPTY;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                
                
                if (item != ModItems.SKIN && SkinNBTHelper.stackHasSkinData(stack)) {
                    if (!skinStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    skinStack = stack;
                } else if (item == ModItems.SKIN_TEMPLATE & !SkinNBTHelper.stackHasSkinData(stack)) {
                    if (!blackStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    blackStack = stack;
                } else {
                    return ItemStack.EMPTY;
                }
                
            }
        }
        
        if (!skinStack.isEmpty() && !blackStack.isEmpty()) {
            ItemStack returnStack = new ItemStack(ModItems.SKIN, 1);
            SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), new SkinDye(skinData.getSkinDye()));
            return returnStack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void onCraft(IInventory inventory) {
        if (!ConfigHandler.enableRecoveringSkins) {
            return;
        }
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            Item item = stack.getItem();
            if (item == ModItems.SKIN_TEMPLATE & !SkinNBTHelper.stackHasSkinData(stack)) {
                inventory.decrStackSize(slotId, 1);
            }
        }
    }
}
