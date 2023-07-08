package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;

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
        return itemStack.is(ModItems.SKIN_TEMPLATE.get());
    }

    protected boolean isValidSkin(ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }
}
