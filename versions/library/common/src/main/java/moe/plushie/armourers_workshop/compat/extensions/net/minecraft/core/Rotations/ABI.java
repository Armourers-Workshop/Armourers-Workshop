package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.core.Rotations;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.Rotations;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class ABI {

    public static float x(@This Rotations rot) {
        return rot.getX();
    }

    public static float y(@This Rotations rot) {
        return rot.getY();
    }

    public static float z(@This Rotations rot) {
        return rot.getZ();
    }
}
