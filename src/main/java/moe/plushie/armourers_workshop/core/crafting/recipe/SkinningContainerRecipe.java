package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.item.ItemStack;

public class SkinningContainerRecipe extends SkinningRecipe {

    public SkinningContainerRecipe(ISkinType skinType) {
        super(skinType);
    }

    @Override
    protected ItemStack build(ItemStack targetStack, ItemStack skinStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(skinStack);
// ItemStack returnStack = SkinNBTHelper.makeArmouerContainerStack(sd);
        return descriptor.asItemStack();
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        return AWItems.ARMOUR_CONTAINER == itemStack.getItem();
    }
}
