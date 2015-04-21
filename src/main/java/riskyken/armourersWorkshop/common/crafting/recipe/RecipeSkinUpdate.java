package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.data.EquipmentSkinTypeData;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class RecipeSkinUpdate implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack oldSkinStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (item == ModItems.equipmentSkin & EquipmentNBTHelper.stackHasLegacySkinData(stack)) {
                    if (oldSkinStack != null) {
                        return false;
                    }
                    oldSkinStack = stack;
                } else {
                    return false;
                }
                
            }
        }
        
        return oldSkinStack != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack oldSkinStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (item == ModItems.equipmentSkin & EquipmentNBTHelper.stackHasLegacySkinData(stack)) {
                    if (oldSkinStack != null) {
                        return null;
                    }
                    oldSkinStack = stack;
                } else {
                    return null;
                }
                
            }
        }
        
        if  (oldSkinStack != null) {
            int skinId = EquipmentNBTHelper.getLegacyIdFromStack(oldSkinStack);
            EquipmentSkinTypeData equipmentItemData = EquipmentDataCache.INSTANCE.getEquipmentData(skinId);
            if (equipmentItemData == null) {
                return null;
            }
            ItemStack returnStack = EquipmentNBTHelper.makeEquipmentSkinStack(equipmentItemData);
            return returnStack;
        } else {
            return null;
        }
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
