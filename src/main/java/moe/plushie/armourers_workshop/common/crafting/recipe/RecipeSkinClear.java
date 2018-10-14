package moe.plushie.armourers_workshop.common.crafting.recipe;

import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeSkinClear extends RecipeItemSkinning {

    public RecipeSkinClear() {
        super(null);
    }

    @Override
    public boolean matches(IInventory inventory) {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinItemStack = null;
        ItemStack soapStack = null;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                
                if (item != ModItems.Skin && SkinNBTHelper.stackHasSkinData(stack) && SkinNBTHelper.getSkinDescriptorFromStack(stack).lockSkin) {
                    if (skinItemStack != null) {
                        return null;
                    }
                    skinItemStack = stack;
                } else if (item == ModItems.soap) {
                    if (soapStack != null) {
                        return null;
                    }
                    soapStack = stack;
                } else {
                    return null;
                }
            }
        }
        
        if (skinItemStack != null && soapStack != null) {
            ItemStack returnStack = skinItemStack.copy();
            SkinNBTHelper.removeSkinDataFromStack(returnStack, true);
            return returnStack;
        } else {
            return null;
        }
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack.getItem() != ModItems.soap) {
                inventory.decrStackSize(slotId, 1);
            }
        }
    }
}
