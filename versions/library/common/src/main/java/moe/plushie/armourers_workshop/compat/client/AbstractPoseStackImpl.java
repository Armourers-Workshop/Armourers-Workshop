package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

@Available("[1.20, )")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractPoseStackImpl {

    protected static final ObjectPool<OpenPoseStack> REUSABLE_QUEUE = ObjectPool.create(OpenPoseStack::new);

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    private static final Quaternionf CONVERTER_QUAT = new Quaternionf();

    private static final FloatBuffer BUFFER3x3 = MatrixUtils.createFloatBuffer(9);
    private static final FloatBuffer BUFFER4x4 = MatrixUtils.createFloatBuffer(16);

    public static IMatrix3f convertMatrix(Matrix3f mat) {
        return new Matrix3fWrapper(mat);
    }

    public static IMatrix4f convertMatrix(Matrix4f mat) {
        return new Matrix4fWrapper(mat);
    }

    public static Matrix3f convertMatrix(IMatrix3f mat) {
        if (mat instanceof Matrix3fWrapper newValue) {
            return newValue.mat;
        }
        mat.store(BUFFER3x3);
        CONVERTER_MAT3.set(BUFFER3x3);
        return CONVERTER_MAT3;
    }

    public static Matrix4f convertMatrix(IMatrix4f mat) {
        if (mat instanceof Matrix4fWrapper newValue) {
            return newValue.mat;
        }
        mat.store(BUFFER4x4);
        CONVERTER_MAT4.set(BUFFER4x4);
        return CONVERTER_MAT4;
    }

    public static Quaternionf convertQuaternion(IQuaternionf q) {
        CONVERTER_QUAT.set(q.x(), q.y(), q.z(), q.w());
        return CONVERTER_QUAT;
    }

    public static Quaternionf copyQuaternion(IQuaternionf q) {
        return new Quaternionf(q.x(), q.y(), q.z(), q.w());
    }

    private static class Matrix3fWrapper implements IMatrix3f {

        private final Matrix3f mat;

        public Matrix3fWrapper(Matrix3f mat) {
            this.mat = mat;
        }

        @Override
        public void load(FloatBuffer buffer) {
            mat.set(buffer);
        }

        @Override
        public void store(FloatBuffer buffer) {
            mat.get(buffer);
        }

        @Override
        public void scale(float x, float y, float z) {
            mat.scale(x, y, z);
        }

        @Override
        public void rotate(IQuaternionf q) {
            mat.rotate(convertQuaternion(q));
        }

        @Override
        public void set(IMatrix3f matrix) {
            mat.set(convertMatrix(matrix));
        }

        public void set(IMatrix4f matrix) {
            mat.set(convertMatrix(matrix));
        }

        @Override
        public void multiply(IMatrix3f matrix) {
            mat.mul(convertMatrix(matrix));
        }

        @Override
        public void multiply(float[] values) {
            var vec = new Vector3f(values[0], values[1], values[2]);
            vec.mul(mat);
            values[0] = vec.x();
            values[1] = vec.y();
            values[2] = vec.z();
        }

        @Override
        public void invert() {
            mat.invert();
        }

        @Override
        public void transpose() {
            mat.transpose();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Matrix3fWrapper that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }

    private static class Matrix4fWrapper implements IMatrix4f {

        private final Matrix4f mat;

        public Matrix4fWrapper(Matrix4f mat) {
            this.mat = mat;
        }

        @Override
        public void load(FloatBuffer buffer) {
            mat.set(buffer);
        }

        @Override
        public void store(FloatBuffer buffer) {
            mat.get(buffer);
        }

        @Override
        public void scale(float x, float y, float z) {
            mat.scale(x, y, z);
        }

        @Override
        public void translate(float x, float y, float z) {
            mat.translate(x, y, z);
        }

        @Override
        public void rotate(IQuaternionf q) {
            mat.rotate(convertQuaternion(q));
        }

        @Override
        public void set(IMatrix4f matrix) {
            mat.set(convertMatrix(matrix));
        }

        @Override
        public void multiply(IMatrix4f matrix) {
            mat.mul(convertMatrix(matrix));
        }

        @Override
        public void multiply(float[] values) {
            var vec = new Vector4f(values[0], values[1], values[2], values[3]);
            vec.mul(mat);
            values[0] = vec.x();
            values[1] = vec.y();
            values[2] = vec.z();
            values[3] = vec.w();
        }

        @Override
        public void invert() {
            mat.invert();
        }

        @Override
        public void transpose() {
            mat.transpose();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Matrix4fWrapper that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }
}
