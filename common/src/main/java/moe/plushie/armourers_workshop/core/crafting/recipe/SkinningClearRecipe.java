package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;

public class SkinningClearRecipe extends SkinningRecipe {

    public SkinningClearRecipe() {
        super(null);
    }

    @Override
    protected ItemStack build(ItemStack targetStack, ItemStack skinStack) {
        ItemStack newItemStack = skinStack.copy();
        newItemStack.setCount(1);
        return SkinItem.replace(newItemStack, ItemStack.EMPTY);
    }

    @Override
    protected boolean isValidTarget(ItemStack targetStack) {
        return targetStack.is(ModItems.SOAP.get());
    }

    @Override
    protected boolean isValidSkin(ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }
}
