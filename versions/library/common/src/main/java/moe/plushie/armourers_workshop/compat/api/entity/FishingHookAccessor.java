package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.projectile.FishingHook;

@Available("[16, )")
public interface FishingHookAccessor extends ProjectileAccessor {

    @Override
    default FishingHook aw2$self() {
        return (FishingHook) this;
    }
}
