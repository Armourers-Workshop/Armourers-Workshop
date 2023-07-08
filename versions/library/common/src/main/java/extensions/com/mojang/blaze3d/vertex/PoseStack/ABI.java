package extensions.com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.PoseStack;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

@Available("[1.20, )")
@Extension
public class ABI {

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    private static final FloatBuffer BUFFER3x3 = ObjectUtils.createFloatBuffer(9);
    private static final FloatBuffer BUFFER4x4 = ObjectUtils.createFloatBuffer(16);

    public static PoseStack copy(@This PoseStack poseStack) {
        PoseStack poseStack1 = new PoseStack();
        poseStack1.last().pose().set(poseStack.last().pose());
        poseStack1.last().normal().set(poseStack.last().normal());
        return poseStack1;
    }

    public static void mulPose(@This PoseStack poseStack, IQuaternionf q) {
        poseStack.mulPose(new Quaternionf(q.x(), q.y(), q.z(), q.w()));
    }

    public static void mulPoseMatrix(@This PoseStack poseStack, IMatrix4f matrix) {
        poseStack.mulPoseMatrix(Mat4.convertMatrix(matrix));
    }

    public static void mulNormalMatrix(@This PoseStack poseStack, IMatrix3f matrix) {
        poseStack.last().normal().mul(Mat3.convertMatrix(matrix));
    }

    public static IMatrix4f lastPose(@This PoseStack poseStack) {
        return new Mat4(poseStack.last().pose());
    }

    public static IMatrix3f lastNormal(@This PoseStack poseStack) {
        return new Mat3(poseStack.last().normal());
    }

    public static class Mat3 implements IMatrix3f {

        private final Matrix3f mat;

        public Mat3(Matrix3f mat) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Mat3 that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }

        public static Matrix3f convertMatrix(IMatrix3f mat) {
            Matrix3f newValue = ObjectUtils.safeCast(mat, Matrix3f.class);
            if (newValue != null) {
                return newValue;
            }
            mat.store(BUFFER3x3);
            CONVERTER_MAT3.set(BUFFER3x3);
            return CONVERTER_MAT3;
        }
    }

    public static class Mat4 implements IMatrix4f {

        private final Matrix4f mat;

        public Mat4(Matrix4f mat) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Mat4 that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }

        public static Matrix4f convertMatrix(IMatrix4f mat) {
            Matrix4f newValue = ObjectUtils.safeCast(mat, Matrix4f.class);
            if (newValue != null) {
                return newValue;
            }
            mat.store(BUFFER4x4);
            CONVERTER_MAT4.set(BUFFER4x4);
            return CONVERTER_MAT4;
        }
    }
}
