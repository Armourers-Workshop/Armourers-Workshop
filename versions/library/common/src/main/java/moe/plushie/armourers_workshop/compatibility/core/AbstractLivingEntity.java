package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Available("[1.18, )")
public abstract class AbstractLivingEntity extends LivingEntity {

    public AbstractLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
}
