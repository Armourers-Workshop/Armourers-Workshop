package moe.plushie.armourers_workshop.compatibility;

import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AbstractMatrix4fWrapper {

    private static final FloatBuffer CONVERTER_BUFFER = BufferUtils.createFloatBuffer(16);
    private static final Matrix4f CONVERTER_MAT = new Matrix4f();

    public static Matrix4f of(IMatrix4f mat) {
        Matrix4f mat2 = ObjectUtils.safeCast(mat, Matrix4f.class);
        if (mat2 != null) {
            return mat2;
        }
        mat.get(CONVERTER_BUFFER);
        CONVERTER_MAT.load(CONVERTER_BUFFER);
        return CONVERTER_MAT;
    }

}
