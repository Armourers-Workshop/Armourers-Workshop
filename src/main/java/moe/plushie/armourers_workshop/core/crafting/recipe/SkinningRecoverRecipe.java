package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.item.ItemStack;

public class SkinningRecoverRecipe extends SkinningRecipe {

    public SkinningRecoverRecipe() {
        super(null);
    }

//    @Override
//    public boolean matches(IInventory inventory) {
//        return !getCraftingResult(inventory).isEmpty();
//    }
//
//    @Override
//    public ItemStack getCraftingResult(IInventory inventory) {
//        if (!ConfigHandler.enableRecoveringSkins) {
//            return ItemStack.EMPTY;
//        }
//        ItemStack skinStack = ItemStack.EMPTY;
//        ItemStack blackStack = ItemStack.EMPTY;
//
//        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
//            ItemStack stack = inventory.getStackInSlot(slotId);
//            if (!stack.isEmpty()) {
//                Item item = stack.getItem();
//
//
//                if (item != ModItems.SKIN && SkinNBTHelper.stackHasSkinData(stack)) {
//                    if (!skinStack.isEmpty()) {
//                        return ItemStack.EMPTY;
//                    }
//                    skinStack = stack;
//                } else if (item == ModItems.SKIN_TEMPLATE & !SkinNBTHelper.stackHasSkinData(stack)) {
//                    if (!blackStack.isEmpty()) {
//                        return ItemStack.EMPTY;
//                    }
//                    blackStack = stack;
//                } else {
//                    return ItemStack.EMPTY;
//                }
//
//            }
//        }
//
//        if (!skinStack.isEmpty() && !blackStack.isEmpty()) {
//            ItemStack returnStack = new ItemStack(ModItems.SKIN, 1);
//            SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
//            SkinNBTHelper.addSkinDataToStack(returnStack, skinData.getIdentifier(), new SkinDye(skinData.getSkinDye()));
//            return returnStack;
//        }
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public void onCraft(IInventory inventory) {
//        if (!ConfigHandler.enableRecoveringSkins) {
//            return;
//        }
//        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
//            ItemStack stack = inventory.getStackInSlot(slotId);
//            Item item = stack.getItem();
//            if (item == ModItems.SKIN_TEMPLATE & !SkinNBTHelper.stackHasSkinData(stack)) {
//                inventory.decrStackSize(slotId, 1);
//            }
//        }
//    }


    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        return ModItems.SKIN_TEMPLATE == itemStack.getItem();
    }

    protected boolean isValidSkin(ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }
}
