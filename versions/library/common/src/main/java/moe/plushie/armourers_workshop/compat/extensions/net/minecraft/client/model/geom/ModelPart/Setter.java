package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.model.geom.ModelPart;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Rotations;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class Setter {

    public static void setRotation(@This ModelPart part, Rotations rot) {
        part.xRot = OpenMath.toRadians(rot.x());
        part.yRot = OpenMath.toRadians(rot.y());
        part.zRot = OpenMath.toRadians(rot.z());
    }
}
