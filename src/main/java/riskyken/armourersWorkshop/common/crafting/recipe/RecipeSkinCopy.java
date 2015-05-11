package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

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
                
                if (item == ModItems.equipmentSkin && EquipmentNBTHelper.stackHasSkinData(stack)) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (item == ModItems.equipmentSkinTemplate & !EquipmentNBTHelper.stackHasSkinData(stack)) {
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
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(skinStack);
            EquipmentNBTHelper.addSkinDataToStack(returnStack, skinData.skinType, skinData.skinId, false);
            return returnStack;
        }
        return null;
    }
    
    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            Item item = stack.getItem();
            if (item == ModItems.equipmentSkinTemplate & !EquipmentNBTHelper.stackHasSkinData(stack)) {
                inventory.decrStackSize(slotId, 1);
            }
        }
    }
}
