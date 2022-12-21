package moe.plushie.armourers_workshop.compatibility.ext;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

@Available("[1.16, 1.19.3)")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractMatrixUtilsExt_V1618 {

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    private static final FloatBuffer CONVERTER_BUFFER3 = BufferUtils.createFloatBuffer(9);
    private static final FloatBuffer CONVERTER_BUFFER4 = BufferUtils.createFloatBuffer(16);

    public static Matrix3f of(IMatrix3f mat) {
        Matrix3f mat2 = ObjectUtils.safeCast(mat, Matrix3f.class);
        if (mat2 != null) {
            return mat2;
        }
        IMatrix3f accessor = ObjectUtils.unsafeCast(CONVERTER_MAT3);
        mat.store(CONVERTER_BUFFER3);
        accessor.load(CONVERTER_BUFFER3);
        return CONVERTER_MAT3;
    }

    public static Matrix4f of(IMatrix4f mat) {
        Matrix4f mat2 = ObjectUtils.safeCast(mat, Matrix4f.class);
        if (mat2 != null) {
            return mat2;
        }
        IMatrix4f accessor = ObjectUtils.unsafeCast(CONVERTER_MAT4);
        mat.store(CONVERTER_BUFFER4);
        accessor.load(CONVERTER_BUFFER4);
        return CONVERTER_MAT4;
    }

    public static Quaternion of(IQuaternionf qat) {
        return new Quaternion(qat.x(), qat.y(), qat.z(), qat.w());
    }

    public static IMatrix3f of(Matrix3f mat) {
        return ObjectUtils.unsafeCast(mat);
    }

    public static IMatrix4f of(Matrix4f mat) {
        return ObjectUtils.unsafeCast(mat);
    }

    public static IPoseStack of(PoseStack poseStack) {
        return (IPoseStack) poseStack;
    }

    public static IPoseStack stack() {
        return of(new PoseStack());
    }
}
