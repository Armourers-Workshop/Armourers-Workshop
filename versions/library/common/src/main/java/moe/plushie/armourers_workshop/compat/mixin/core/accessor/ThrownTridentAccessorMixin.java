package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.ThrownTridentAccessor;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(ThrownTrident.class)
public abstract class ThrownTridentAccessorMixin implements ThrownTridentAccessor {
}
