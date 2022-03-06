package moe.plushie.armourers_workshop.core.skin.data.adapter;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class LivingEntityAdapter<T extends LivingEntity> extends SkinAdapter<T> {


    public LivingEntityAdapter(EntityType<T> entityType) {
        super(entityType);
    }
}
