package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface RegisterEntityAttributesEvent {

    void register(EntityType<? extends LivingEntity> entity, AttributeSupplier.Builder builder);
}
