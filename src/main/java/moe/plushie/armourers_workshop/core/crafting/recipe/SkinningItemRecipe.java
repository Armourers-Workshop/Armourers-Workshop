package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.item.ItemStack;

public class SkinningItemRecipe extends SkinningRecipe {

    private ISkinToolType toolType;

    public SkinningItemRecipe(ISkinType skinType) {
        super(skinType);
        if (skinType instanceof ISkinToolType) {
            this.toolType = (ISkinToolType) skinType;
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (toolType != null) {
            return toolType.contains(itemStack.getItem());
        }
        return super.isValidTarget(itemStack);
    }
}
