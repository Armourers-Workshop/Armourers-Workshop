package moe.plushie.armourers_workshop.common.crafting.recipe;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeSkinItem extends RecipeItemSkinning {
    
    private final ItemOverrideType overrideType;
    
    public RecipeSkinItem(ISkinType skinType, ItemOverrideType overrideType) {
        super(skinType);
        this.overrideType = overrideType;
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        return !getCraftingResult(inventory).isEmpty();
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = ItemStack.EMPTY;
        ItemStack itemStack = ItemStack.EMPTY;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                
                if (isValidSkinForType(stack)) {
                    if (!skinStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    skinStack = stack;
                    continue;
                }
                
                if (ModAddonManager.isOverrideItem(overrideType, item)) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemStack = stack;
                    continue;
                }
                
                return ItemStack.EMPTY;
            }
        }
        
        if (!skinStack.isEmpty() && !itemStack.isEmpty()) {
            ItemStack returnStack = itemStack.copy();
            
            SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), skinData.getSkinDye());
            
            return returnStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            inventory.decrStackSize(slotId, 1);
        }
    }
}
