package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

import java.nio.FloatBuffer;

@Available("[1.18, )")
public class AbstractForgeEpicFightHandlerImpl {

    public static void copyTo(Joint joint, OpenMatrix4f[] poses, FloatBuffer buf) {
        var mat = OpenMatrix4f.mul(poses[joint.getId()], joint.getToOrigin(), null);
        buf.put(mat.m00);
        buf.put(mat.m01);
        buf.put(mat.m02);
        buf.put(mat.m03);
        buf.put(mat.m10);
        buf.put(mat.m11);
        buf.put(mat.m12);
        buf.put(mat.m13);
        buf.put(mat.m20);
        buf.put(mat.m21);
        buf.put(mat.m22);
        buf.put(mat.m23);
        buf.put(mat.m30);
        buf.put(mat.m31);
        buf.put(mat.m32);
        buf.put(mat.m33);
        buf.rewind();
    }
}
