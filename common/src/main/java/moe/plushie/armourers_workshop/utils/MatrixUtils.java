package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.compatibility.AbstractMatrixUtils;
import moe.plushie.armourers_workshop.utils.math.Matrix3f;
import moe.plushie.armourers_workshop.utils.math.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.FloatBuffer;

@Environment(value = EnvType.CLIENT)
public class MatrixUtils extends AbstractMatrixUtils {

    public static IMatrix4f mat4(FloatBuffer buffer) {
        Matrix4f mat = new Matrix4f();
        mat.load(buffer);
        return mat;
    }

    public static IMatrix3f mat3(FloatBuffer buffer) {
        Matrix3f mat = new Matrix3f();
        if (buffer.remaining() == 9) {
            mat.load(buffer);
        } else {
            mat.import44(buffer);
        }
        return mat;
    }

}
