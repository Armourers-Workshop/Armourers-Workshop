package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.init.ModItemTags;
import net.minecraft.world.item.ItemStack;

public class SkinningHorseArmorRecipe extends SkinningRecipe {

    public SkinningHorseArmorRecipe(ISkinType skinType) {
        super(skinType);
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        return ModItemTags.HORSE_ARMORS.get().contains(itemStack);
    }
}
