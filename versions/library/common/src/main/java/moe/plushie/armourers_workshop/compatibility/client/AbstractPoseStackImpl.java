package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractPoseStackImpl {

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    private static final FloatBuffer BUFFER3x3 = ObjectUtils.createFloatBuffer(9);
    private static final FloatBuffer BUFFER4x4 = ObjectUtils.createFloatBuffer(16);

    public static Matrix3f convertMatrix(IMatrix3f mat) {
        AbstractMatrix3f newValue = ObjectUtils.safeCast(mat, AbstractMatrix3f.class);
        if (newValue != null) {
            return newValue.mat;
        }
        mat.store(BUFFER3x3);
        CONVERTER_MAT3.set(BUFFER3x3);
        return CONVERTER_MAT3;
    }

    public static Matrix4f convertMatrix(IMatrix4f mat) {
        AbstractMatrix4f newValue = ObjectUtils.safeCast(mat, AbstractMatrix4f.class);
        if (newValue != null) {
            return newValue.mat;
        }
        mat.store(BUFFER4x4);
        CONVERTER_MAT4.set(BUFFER4x4);
        return CONVERTER_MAT4;
    }

    public static Quaternionf convertQuaternion(IQuaternionf q) {
        return new Quaternionf(q.x(), q.y(), q.z(), q.w());
    }

    public static class AbstractMatrix3f implements IMatrix3f {

        private final Matrix3f mat;

        public AbstractMatrix3f(Matrix3f mat) {
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
            mat.rotate(new Quaternionf(q.x(), q.y(), q.z(), q.w()));
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
            Vector3f vec = new Vector3f(values[0], values[1], values[2]);
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
            if (!(o instanceof AbstractMatrix3f that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }

    public static class AbstractMatrix4f implements IMatrix4f {

        private final Matrix4f mat;

        public AbstractMatrix4f(Matrix4f mat) {
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
            mat.rotate(new Quaternionf(q.x(), q.y(), q.z(), q.w()));
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
            Vector4f vec = new Vector4f(values[0], values[1], values[2], values[3]);
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
            if (!(o instanceof AbstractMatrix4f that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }
}
