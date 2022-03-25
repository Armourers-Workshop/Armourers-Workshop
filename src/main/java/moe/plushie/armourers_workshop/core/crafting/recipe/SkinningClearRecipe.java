package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.init.common.AWItems;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.item.ItemStack;

public class SkinningClearRecipe extends SkinningRecipe {

    public SkinningClearRecipe() {
        super(null);
    }

    @Override
    protected ItemStack build(ItemStack targetStack, ItemStack skinStack) {
        return SkinItem.replace(skinStack.copy(), ItemStack.EMPTY);
    }

    @Override
    protected boolean isValidTarget(ItemStack targetStack) {
        return AWItems.SOAP == targetStack.getItem();
    }

    @Override
    protected boolean isValidSkin(ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }
}
