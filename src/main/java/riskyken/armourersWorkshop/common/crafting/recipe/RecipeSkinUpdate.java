package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.AbstractModItemArmour;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class RecipeSkinUpdate implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack oldSkinStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (SkinNBTHelper.stackHasLegacySkinData(stack)) {
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
                
                if (SkinNBTHelper.stackHasLegacySkinData(stack)) {
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
            int skinId = SkinNBTHelper.getLegacyIdFromStack(oldSkinStack);
            ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(oldSkinStack.getItemDamage());
            
            SkinPointer skinPointer = new SkinPointer(skinType, skinId, false);
            
            if (oldSkinStack.getItem() instanceof AbstractModItemArmour) {
                return SkinNBTHelper.makeArmouerContainerStack(skinPointer);
            } else {
                return SkinNBTHelper.makeEquipmentSkinStack(skinPointer);
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

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        // TODO Auto-generated method stub
        return null;
    }
}
