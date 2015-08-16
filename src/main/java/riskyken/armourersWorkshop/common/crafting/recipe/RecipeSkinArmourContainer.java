package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class RecipeSkinArmourContainer extends RecipeItemSkinning {

    public RecipeSkinArmourContainer(ISkinType skinType) {
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
                } else if (stack.getItem() == ModItems.armourContainerItem) {
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
            SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(skinStack);
            ItemStack returnStack = EquipmentNBTHelper.makeArmouerContainerStack(skinPointer);
            return returnStack;
        } else {
            return null;
        }
    }
    
    private boolean isValidArmourForSkin(ItemStack armourStack, ItemStack skinStack) {
        SkinPointer sp = EquipmentNBTHelper.getSkinPointerFromStack(skinStack);
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
