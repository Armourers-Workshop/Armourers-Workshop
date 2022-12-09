package moe.plushie.armourers_workshop.compatibility;

import com.mojang.math.Matrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AbstractMatrix3fWrapper {

    private static final FloatBuffer CONVERTER_BUFFER = BufferUtils.createFloatBuffer(9);
    private static final Matrix3f CONVERTER_MAT = new Matrix3f();

    public static Matrix3f of(IMatrix3f mat) {
        Matrix3f mat2 = ObjectUtils.safeCast(mat, Matrix3f.class);
        if (mat2 != null) {
            return mat2;
        }
        IMatrix3f accessor = ObjectUtils.unsafeCast(CONVERTER_MAT);
        mat.get(CONVERTER_BUFFER);
        accessor.set(CONVERTER_BUFFER);
        return CONVERTER_MAT;
    }

}
