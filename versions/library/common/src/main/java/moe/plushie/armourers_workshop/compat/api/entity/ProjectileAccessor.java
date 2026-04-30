package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.projectile.Projectile;

@Available("[16, )")
public interface ProjectileAccessor extends EntityAccessor {

    @Override
    default Projectile aw2$self() {
        return (Projectile) this;
    }
}
