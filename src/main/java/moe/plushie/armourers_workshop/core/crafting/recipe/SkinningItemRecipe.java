package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class SkinningItemRecipe extends SkinningRecipe {

    private ITag<Item> itemTag;

    public SkinningItemRecipe(ISkinType skinType) {
        super(skinType);
        if (skinType instanceof ISkinToolType) {
            this.itemTag = ((ISkinToolType) skinType).getTag();
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (itemTag != null) {
            return itemStack.getItem().is(itemTag);
        }
        return super.isValidTarget(itemStack);
    }
}
