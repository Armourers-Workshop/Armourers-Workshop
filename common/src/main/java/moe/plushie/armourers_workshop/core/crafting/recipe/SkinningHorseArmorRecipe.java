package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.init.ModItemTags;
import net.minecraft.world.item.ItemStack;

public class SkinningHorseArmorRecipe extends SkinningRecipe {

    public SkinningHorseArmorRecipe(SkinType skinType) {
        super(skinType);
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        return itemStack.is(ModItemTags.HORSE_ARMORS.get());
    }
}
