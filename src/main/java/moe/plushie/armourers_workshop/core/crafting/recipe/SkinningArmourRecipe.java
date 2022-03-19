package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class SkinningArmourRecipe extends SkinningRecipe {

    private EquipmentSlotType slotType;

    public SkinningArmourRecipe(ISkinType skinType) {
        super(skinType);
        if (skinType instanceof ISkinArmorType) {
            slotType = ((ISkinArmorType) skinType).getSlotType();
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (slotType != null) {
            return MobEntity.isValidSlotForItem(slotType, itemStack);
        }
        return false;
    }
}
