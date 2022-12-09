package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AbstractMatrix3fWrapper implements IMatrix3f {

    private static final FloatBuffer CONVERTER_BUFFER = BufferUtils.createFloatBuffer(9);
    private static final Matrix3f CONVERTER_MAT = new Matrix3f();

    private final Matrix3f mat;

    public AbstractMatrix3fWrapper(Matrix3f mat) {
        this.mat = mat;
    }

    public static Matrix3f of(IMatrix3f mat) {
        if (mat instanceof Matrix3f) {
            return (Matrix3f) mat;
        }
        if (mat instanceof AbstractMatrix3fWrapper) {
            return ((AbstractMatrix3fWrapper) mat).mat;
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
    public void multiply(IMatrix3f matrix) {
        mat.mul(of(matrix));
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
    public IMatrix3f copy() {
        return new AbstractMatrix3fWrapper(new Matrix3f(mat));
    }
}
