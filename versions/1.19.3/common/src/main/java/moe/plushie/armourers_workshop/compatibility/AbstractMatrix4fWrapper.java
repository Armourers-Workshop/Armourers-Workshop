package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AbstractMatrix4fWrapper implements IMatrix4f {

    private static final FloatBuffer CONVERTER_BUFFER = BufferUtils.createFloatBuffer(16);
    private static final Matrix4f CONVERTER_MAT = new Matrix4f();

    private final Matrix4f mat;

    public AbstractMatrix4fWrapper(Matrix4f mat) {
        this.mat = mat;
    }

    public static Matrix4f of(IMatrix4f mat) {
        if (mat instanceof Matrix4f) {
            return (Matrix4f) mat;
        }
        if (mat instanceof AbstractMatrix4fWrapper) {
            return ((AbstractMatrix4fWrapper) mat).mat;
        }
        mat.get(CONVERTER_BUFFER);
        CONVERTER_MAT.set(CONVERTER_BUFFER);
        return CONVERTER_MAT;
    }

    @Override
    public void set(FloatBuffer buffer) {
        mat.set(buffer);
    }

    @Override
    public void get(FloatBuffer buffer) {
        mat.get(buffer);
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        mat.rotate(new Quaternionf(quaternion.i(), quaternion.j(), quaternion.k(), quaternion.r()));
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        mat.mul(of(matrix));
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
    public IMatrix4f copy() {
        return new AbstractMatrix4fWrapper(new Matrix4f(mat));
    }
}
