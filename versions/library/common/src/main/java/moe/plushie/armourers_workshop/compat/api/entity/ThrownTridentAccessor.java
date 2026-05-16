package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.projectile.ThrownTrident;

@Available("[16, 26)")
public interface ThrownTridentAccessor extends ArrowAccessor {

    @Override
    default ThrownTrident aw2$self() {
        return (ThrownTrident) this;
    }
}
