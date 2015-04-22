package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;

public class RecipeSkinCopy implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack skinStack = null;
        ItemStack blackStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (EquipmentNBTHelper.stackHasSkinData(stack) && EquipmentNBTHelper.getSkinNBTDataFromStack(stack).lockSkin && item != ModItems.equipmentSkin) {
                    if (skinStack != null) {
                        return false;
                    }
                    skinStack = stack;
                } else if (item == ModItems.equipmentSkinTemplate & !EquipmentNBTHelper.stackHasSkinData(stack)) {
                    if (blackStack != null) {
                        return false;
                    }
                    blackStack = stack;
                } else {
                    return false;
                }
                
            }
        }
        
        return skinStack != null && blackStack != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack skinStack = null;
        ItemStack blackStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (EquipmentNBTHelper.stackHasSkinData(stack) && EquipmentNBTHelper.getSkinNBTDataFromStack(stack).lockSkin && item != ModItems.equipmentSkin) {
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
            SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(skinStack);
            EquipmentNBTHelper.addSkinDataToStack(returnStack, skinData.skinType, skinData.skinId, false);
            return returnStack;
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
