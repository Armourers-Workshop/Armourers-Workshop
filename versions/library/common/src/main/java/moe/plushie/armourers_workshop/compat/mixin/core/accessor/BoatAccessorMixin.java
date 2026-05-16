package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.BoatAccessor;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, 26)")
@Mixin(Boat.class)
public abstract class BoatAccessorMixin implements BoatAccessor {
}
