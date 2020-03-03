package moe.plushie.armourers_workshop.common.crafting.recipe;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeSkinArmourContainer extends RecipeItemSkinning {

    public RecipeSkinArmourContainer(ISkinType skinType) {
        super(skinType);
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        return !getCraftingResult(inventory).isEmpty();
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack skinStack = ItemStack.EMPTY;
        ItemStack armourStack = ItemStack.EMPTY;
        
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            ItemStack stack = inventory.getStackInSlot(slotId);
            if (stack != ItemStack.EMPTY) {
                Item item = stack.getItem();
                
                if (isValidSkinForType(stack)) {
                    if (skinStack != ItemStack.EMPTY) {
                        return ItemStack.EMPTY;
                    }
                    skinStack = stack;
                } else if (stack.getItem() == ModItems.ARMOUR_CONTAINER_ITEM) {
                    if (armourStack != ItemStack.EMPTY) {
                        return ItemStack.EMPTY;
                    }
                    armourStack = stack;
                } else {
                    return ItemStack.EMPTY;
                }
                
            }
        }
        
        if (skinStack != ItemStack.EMPTY && armourStack != ItemStack.EMPTY) {
            SkinDescriptor sd = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
            ItemStack returnStack = SkinNBTHelper.makeArmouerContainerStack(sd);
            return returnStack;
        } else {
            return ItemStack.EMPTY;
        }
    }
    
    private boolean isValidArmourForSkin(ItemStack armourStack, ItemStack skinStack) {
        SkinDescriptor sd = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
        ISkinType skinType = sd.getIdentifier().getSkinType();
        Item armourItem = armourStack.getItem();
        if (armourItem.isValidArmor(armourStack, EntityEquipmentSlot.values()[skinType.getVanillaArmourSlotId()], null)) {
            return true;
        }
        return false;
    }

    @Override
    public void onCraft(IInventory inventory) {
        for (int slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
            inventory.decrStackSize(slotId, 1);
        }
    }
}
