package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.ProjectileAccessor;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(Projectile.class)
public abstract class ProjectileAccessorMixin implements ProjectileAccessor {
}
