package moe.plushie.armourers_workshop.common.crafting.recipe;

public class RecipeSkinUpdate /*implements IRecipe*/ {/*

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
            
            SkinPointer skinPointer = new SkinPointer(new SkinIdentifier(skinId, null, 0, skinType), false);
            
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
    }*/
}
