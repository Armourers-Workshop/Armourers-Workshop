package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.LivingEntityAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(LivingEntity.class)
public abstract class LivingEntityAccessorMixin implements LivingEntityAccessor {
}
