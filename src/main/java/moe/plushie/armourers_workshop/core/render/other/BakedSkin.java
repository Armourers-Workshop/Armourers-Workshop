package moe.plushie.armourers_workshop.core.render.other;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.action.IHasTag;
import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import javax.annotation.Nullable;

public class BakedSkin implements IBakedSkin {

    public Skin skin;
    public SkinDye skinDye;

    public  BakedSkin(Skin skin, SkinDye skinDye) {
        this.skin = skin;
        this.skinDye = skinDye;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public SkinDye getSkinDye() {
        return skinDye;
    }


    public boolean isOverride(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        ISkinType skinType = skin.getType();
        if (skinType instanceof IHasTag) {
            return itemStack.getItem().is(((IHasTag)skinType).getTag());
        }
        return false;
    }


    public boolean hasEquipment(EquipmentSlotType type) {
        return true;
    }
    public boolean hasEquipment(EquipmentSlotType.Group group) {
        return true;
    }

}
