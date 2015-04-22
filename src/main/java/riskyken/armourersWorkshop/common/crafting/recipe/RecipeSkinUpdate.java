package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.AbstractModItemArmour;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class RecipeSkinUpdate implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack oldSkinStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (EquipmentNBTHelper.stackHasLegacySkinData(stack)) {
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
                
                if (EquipmentNBTHelper.stackHasLegacySkinData(stack)) {
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
            Skin equipmentItemData = SkinDataCache.INSTANCE.getEquipmentData(skinId);
            if (equipmentItemData == null) {
                return null;
            }
            if (oldSkinStack.getItem() instanceof AbstractModItemArmour) {
                return EquipmentNBTHelper.makeArmouerContainerStack(equipmentItemData);
            } else {
                return EquipmentNBTHelper.makeEquipmentSkinStack(equipmentItemData);
            }
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
