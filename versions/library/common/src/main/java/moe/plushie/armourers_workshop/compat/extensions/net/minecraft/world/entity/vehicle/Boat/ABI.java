package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.vehicle.Boat;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.vehicle.Boat;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[20, 26)")
@Extension
public class ABI {

    public static Boat.Type variant(@This Boat entity) {
        return entity.getVariant();
    }
}
