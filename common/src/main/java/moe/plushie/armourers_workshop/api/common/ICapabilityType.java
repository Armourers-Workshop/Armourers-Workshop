package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.Entity;

import java.util.Optional;

public interface ICapabilityType<T> {

    Optional<T> get(Entity entity);
}
