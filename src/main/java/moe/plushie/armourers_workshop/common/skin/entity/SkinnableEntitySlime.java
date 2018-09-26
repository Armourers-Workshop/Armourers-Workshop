package moe.plushie.armourers_workshop.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;

public class SkinnableEntitySlime extends SkinnableEntity {

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySlime.class;
    }

    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinHead);
    }
    
    @Override
    public int getSlotsForSkinType(ISkinType skinType) {
        return 1;
    }
}
