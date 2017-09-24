package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class RecipeSkinArmour extends RecipeItemSkinning {

    public RecipeSkinArmour(ISkinType skinType) {
        super(skinType);
    }

    @Override
    public boolean matches(IInventory inventory) {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = null;
        ItemStack armourStack = null;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (isValidSkinForType(stack)) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (isValidArmour(stack) &
                         !SkinNBTHelper.isSkinLockedOnStack(stack)) {
                    if (armourStack != null) {
                        return null;
                    }
                    armourStack = stack;
                } else {
                    return null;
                }
                
            }
        }
        
        if (skinStack != null && armourStack != null) {
            if (!isValidArmourForSkin(armourStack, skinStack)) {
                return null;
            }
            ItemStack returnStack = armourStack.copy();
            
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(skinStack);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), skinData.getSkinDye(), true);
            
            return returnStack;
        } else {
            return null;
        }
    }
    
    private boolean isValidArmour(ItemStack stack) {
        Item item = stack.getItem();
        for (int i = 0; i < 4; i++) {
            if (item.isValidArmor(stack, i, null)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidArmourForSkin(ItemStack armourStack, ItemStack skinStack) {
        SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        ISkinType skinType = sp.getSkinType();
        Item armourItem = armourStack.getItem();
        if (armourItem.isValidArmor(armourStack, skinType.getVanillaArmourSlotId(), null)) {
            return true;
        }
        return false;
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            inventory.setInventorySlotContents(slotId, null);
        }
    }
}
