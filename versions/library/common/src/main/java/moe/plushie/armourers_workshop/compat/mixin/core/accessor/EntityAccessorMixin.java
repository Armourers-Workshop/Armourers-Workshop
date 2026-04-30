package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.EntityAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(Entity.class)
public abstract class EntityAccessorMixin implements EntityAccessor {
}
