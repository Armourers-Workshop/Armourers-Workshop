package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class SkinningArmourRecipe extends SkinningRecipe {

    private OpenEquipmentSlot slotType;

    public SkinningArmourRecipe(SkinType skinType) {
        super(skinType);
        if (skinType instanceof SkinType.Armor armorType) {
            slotType = armorType.slotType();
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (slotType != null) {
            return slotType == itemStack.getEquipmentSlot();
        }
        return false;
    }
}
