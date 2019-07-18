package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.addons.ModAddon.ItemOverrideType;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.wardrobe.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class RecipeSkinSword extends RecipeItemSkinning {
    
    public RecipeSkinSword() {
        super(SkinTypeRegistry.skinSword);
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        return getCraftingResult(inventory) != null;
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = null;
        ItemStack swordStack = null;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (isValidSkinForType(stack)) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (EntityEquipmentDataManager.INSTANCE.isRenderItem(ItemOverrideType.SWORD, item) &
                         !SkinNBTHelper.isSkinLockedOnStack(stack)) {
                    if (swordStack != null) {
                        return null;
                    }
                    swordStack = stack;
                } else {
                    return null;
                }
                
            }
        }
        
        if (skinStack != null && swordStack != null) {
            ItemStack returnStack = swordStack.copy();
            
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(skinStack);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), skinData.getSkinDye(), true);
            
            return returnStack;
        } else {
            return null;
        }
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            inventory.decrStackSize(slotId, 1);
        }
    }
}
