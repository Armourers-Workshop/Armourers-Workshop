package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityDataBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

@Available("[1.21, )")
public class AbstractLivingEntityImpl {

    public static abstract class CustomLivingEntity extends LivingEntity {

        public CustomLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }
    }

    public static abstract class CustomArmorStand extends ArmorStand {

        public CustomArmorStand(EntityType<? extends CustomArmorStand> entityType, Level level) {
            super(entityType, level);
        }

        protected void defineSynchedData(IEntityDataBuilder builder) {
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            super.defineSynchedData(builder);
            this.defineSynchedData(builder::define);
        }
    }
}
