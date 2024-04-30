package moe.plushie.armourers_workshop.compatibility.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class AbstractLivingEntity extends AbstractLivingEntityImpl.CustomLivingEntity {

    public AbstractLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static abstract class ArmorStand extends AbstractLivingEntityImpl.CustomArmorStand {

        public ArmorStand(EntityType<? extends ArmorStand> entityType, Level level) {
            super(entityType, level);
        }
    }
}
