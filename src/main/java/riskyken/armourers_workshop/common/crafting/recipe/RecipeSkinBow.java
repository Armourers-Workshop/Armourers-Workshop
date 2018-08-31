package riskyken.armourers_workshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.addons.ModAddon.ItemOverrideType;
import riskyken.armourers_workshop.common.skin.EntityEquipmentDataManager;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class RecipeSkinBow extends RecipeItemSkinning {
    
    public RecipeSkinBow() {
        super(SkinTypeRegistry.skinBow);
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        return getCraftingResult(inventory) != null;
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = null;
        ItemStack bowStack = null;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (isValidSkinForType(stack)) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (EntityEquipmentDataManager.INSTANCE.isRenderItem(ItemOverrideType.BOW, item) &
                         !SkinNBTHelper.isSkinLockedOnStack(stack)) {
                    if (bowStack != null) {
                        return null;
                    }
                    bowStack = stack;
                } else {
                    return null;
                }
                
            }
        }
        
        if (skinStack != null && bowStack != null) {
            ItemStack returnStack = bowStack.copy();
            
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
