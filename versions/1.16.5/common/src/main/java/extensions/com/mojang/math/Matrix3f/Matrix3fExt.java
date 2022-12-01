package extensions.com.mojang.math.Matrix3f;

import com.mojang.math.Matrix3f;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.utils.ext.OpenMatrix3f;

import java.nio.FloatBuffer;

@Extension
public class Matrix3fExt {

    public static void store(@This Matrix3f matrix, FloatBuffer buffer) {
        OpenMatrix3f.of(matrix).store(buffer);
    }
}
