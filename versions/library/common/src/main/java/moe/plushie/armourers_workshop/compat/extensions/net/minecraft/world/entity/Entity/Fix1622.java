package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, 26)")
@Extension
public class Fix1622 {

    public static float walkDist(@This Entity entity) {
        return entity.walkDist;
    }

    public static float moveDist(@This Entity entity) {
        return entity.moveDist;
    }
}

