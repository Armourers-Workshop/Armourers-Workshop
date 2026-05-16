package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.VehicleEntityAccessor;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[21, )")
@Mixin(VehicleEntity.class)
public abstract class VehicleEntityAccessorMixin implements VehicleEntityAccessor {
}
