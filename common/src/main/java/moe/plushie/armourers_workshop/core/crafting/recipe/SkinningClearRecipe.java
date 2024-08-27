package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;

public class SkinningClearRecipe extends SkinningRecipe {

    public SkinningClearRecipe() {
        super(null);
    }

    @Override
    protected ItemStack build(ItemStack targetStack, ItemStack skinStack, SkinOptions options) {
        var newItemStack = skinStack.copy();
        newItemStack.setCount(1);
        newItemStack.remove(ModDataComponents.SKIN.get());
        return newItemStack;
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
