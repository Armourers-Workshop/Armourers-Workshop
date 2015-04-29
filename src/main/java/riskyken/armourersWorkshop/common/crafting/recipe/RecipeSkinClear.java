package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

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
                
                
                if (item != ModItems.equipmentSkin && EquipmentNBTHelper.stackHasSkinData(stack) && EquipmentNBTHelper.getSkinPointerFromStack(stack).lockSkin) {
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
            EquipmentNBTHelper.removeSkinDataFromStack(returnStack, true);
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
                inventory.setInventorySlotContents(slotId, null);
            }
        }
    }
}
