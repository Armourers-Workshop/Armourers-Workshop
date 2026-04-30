package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.FishingHookAccessor;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(FishingHook.class)
public abstract class FishingHookAccessorMixin implements FishingHookAccessor {
}
