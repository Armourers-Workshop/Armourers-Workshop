package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.vehicle.VehicleEntity;

@Available("[21, )")
public interface VehicleEntityAccessor extends EntityAccessor {

    @Override
    default VehicleEntity aw2$self() {
        return (VehicleEntity) this;
    }
}
