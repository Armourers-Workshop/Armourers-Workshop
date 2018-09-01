package moe.plushie.armourers_workshop.common.crafting.recipe;

public class RecipeSkinDye /*implements IRecipe*/ {/*

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        return getCraftingResult(invCrafting) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack skinStack = null;
        ItemStack dyeStack = null;
        
        for (int slotId = 0; slotId < invCrafting.getSizeInventory(); slotId++) {
            ItemStack stack = invCrafting.getStackInSlot(slotId);
            if (stack != null) {
                Item item = stack.getItem();
                if (item == ModItems.dyeBottle) {
                    if (dyeStack != null) {
                        return null;
                    }
                    if (PaintingHelper.getToolHasPaint(stack)) {
                        dyeStack = stack;
                    } else{
                        return null;
                    }
                } else if (item == ModItems.equipmentSkin) {
                    if (skinStack != null) {
                        return null;
                    }
                    SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
                    if (skinPointer != null) {
                        if (skinPointer.getSkinDye().getNumberOfDyes() < SkinDye.MAX_SKIN_DYES) {
                            skinStack = stack;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        
        if (skinStack != null && dyeStack != null) {
            ItemStack returnStack = skinStack.copy();
            byte[] rgbt = PaintingHelper.getToolPaintData(dyeStack);
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(returnStack);
            ISkinDye dye = skinPointer.getSkinDye();
            dye.addDye(rgbt);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
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
    }*/
}
