package riskyken.armourersWorkshop.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;

public class RecipeSwordSkin implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack skinStack = null;
        ItemStack swordStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (item == ModItems.equipmentSkin && EquipmentNBTHelper.stackHasSkinData(stack) && EquipmentNBTHelper.getSkinTypeFromStack(stack) == SkinTypeRegistry.skinSword) {
                    if (skinStack != null) {
                        return false;
                    }
                    skinStack = stack;
                } else if (EntityEquipmentDataManager.INSTANCE.isSwordRenderItem(item)) {
                    if (swordStack != null) {
                        return false;
                    }
                    swordStack = stack;
                } else {
                    return false;
                }
                
            }
        }
        
        return skinStack != null && swordStack != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack skinStack = null;
        ItemStack swordStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                
                if (item == ModItems.equipmentSkin && EquipmentNBTHelper.stackHasSkinData(stack) && EquipmentNBTHelper.getSkinTypeFromStack(stack) == SkinTypeRegistry.skinSword) {
                    if (skinStack != null) {
                        return null;
                    }
                    skinStack = stack;
                } else if (EntityEquipmentDataManager.INSTANCE.isSwordRenderItem(item)) {
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
            
            SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(skinStack);
            EquipmentNBTHelper.addSkinDataToStack(returnStack, skinData.skinType, skinData.skinId, true);
            
            return returnStack;
        } else {
            return null;
        }
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
    
    /*
        if (!hasItemStackGotEquipmentData(stack)) {
            return null;
        }
        Item item = stack.getItem();
        if (item == ModItems.equipmentSkin) {
            int damage = stack.getItemDamage();
            if (damage >= 0 & damage < SkinTypeRegistry.INSTANCE.getNumberOfSkinRegistered()) {
                return SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(damage);
            }
        }
        if (item == ModItems.armourContainer[0]) {
            return SkinTypeRegistry.skinHead;
        }
        if (item == ModItems.armourContainer[1]) {
            return SkinTypeRegistry.skinChest;
        }
        if (item == ModItems.armourContainer[2]) {
            return SkinTypeRegistry.skinLegs;
        }
        if (item == ModItems.armourContainer[3]) {
            return SkinTypeRegistry.skinFeet;
        }
        return null;
     */
    
}
