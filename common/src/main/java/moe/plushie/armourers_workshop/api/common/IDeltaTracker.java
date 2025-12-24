package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.Entity;

public interface IDeltaTracker {

    boolean isPaused();

    boolean isFrozen();

    float partialTick();

    float partialTick(Entity entity);

    float rate();
}
