package moe.plushie.armourers_workshop.common.crafting.recipe;

public class RecipeSkinArmourContainer/*extends RecipeItemSkinning*/ {/*

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
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(skinStack);
            ItemStack returnStack = SkinNBTHelper.makeArmouerContainerStack(skinPointer);
            return returnStack;
        } else {
            return null;
        }
    }
    
    private boolean isValidArmourForSkin(ItemStack armourStack, ItemStack skinStack) {
        SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        ISkinType skinType = sp.getIdentifier().getSkinType();
        Item armourItem = armourStack.getItem();
        if (armourItem.isValidArmor(armourStack, skinType.getVanillaArmourSlotId(), null)) {
            return true;
        }
        return false;
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            inventory.decrStackSize(slotId, 1);
        }
    }*/
}
