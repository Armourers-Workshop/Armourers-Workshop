package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.ArrowAccessor;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, 26)")
@Mixin(AbstractArrow.class)
public abstract class ArrowAccessorMixin implements ArrowAccessor {
}
