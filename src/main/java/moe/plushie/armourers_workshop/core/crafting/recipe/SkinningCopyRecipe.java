package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.item.ItemStack;

public class SkinningCopyRecipe extends SkinningRecipe {

    public SkinningCopyRecipe() {
        super(null);
    }

    @Override
    protected ItemStack build(ItemStack targetStack, ItemStack skinStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(skinStack);
// ItemStack returnStack = SkinNBTHelper.makeArmouerContainerStack(sd);
        return descriptor.asItemStack();
    }

    @Override
    protected void shrink(ItemStack targetStack, ItemStack skinStack) {
        targetStack.shrink(1);
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        return AWItems.SKIN_TEMPLATE == itemStack.getItem();
    }

    protected boolean isValidSkin(ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }
}
