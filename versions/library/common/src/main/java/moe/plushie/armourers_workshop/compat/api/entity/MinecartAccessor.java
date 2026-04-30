package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.vehicle.Minecart;

@Available("[16, )")
public interface MinecartAccessor extends VehicleEntityAccessor {

    @Override
    default Minecart aw2$self() {
        return (Minecart) this;
    }
}
