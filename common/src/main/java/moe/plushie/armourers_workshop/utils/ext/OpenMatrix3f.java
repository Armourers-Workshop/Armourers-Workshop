package moe.plushie.armourers_workshop.utils.ext;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import java.nio.FloatBuffer;

public class OpenMatrix3f extends Matrix3f {

    public OpenMatrix3f() {
        super();
    }

    public OpenMatrix3f(Quaternion quaternion) {
        super(quaternion);
    }

    public OpenMatrix3f(Matrix3f matrix) {
        super(matrix);
    }

    public OpenMatrix3f(Matrix4f matrix) {
        super(matrix);
    }

    public static OpenMatrix3f createScaleMatrix(float x, float y, float z) {
        OpenMatrix3f matrix = new OpenMatrix3f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        return matrix;
    }

    public static OpenMatrix3f of(Matrix3f other) {
        if (other instanceof OpenMatrix3f) {
            return (OpenMatrix3f) other;
        }
        return new OpenMatrix3f(other);
    }

    public void multiply(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        values[0] = m00 * x + m01 * y + m02 * z;
        values[1] = m10 * x + m11 * y + m12 * z;
        values[2] = m20 * x + m21 * y + m22 * z;
    }

    public void multiply(Matrix3f other) {
        multiply(this, of(other), this);
    }

    public void multiply(Quaternion other) {
        multiply(new OpenMatrix3f(other));
    }

    public void multiplyFront(Matrix3f other) {
        multiply(of(other), this, this);
    }

    public void multiplyFront(Quaternion other) {
        multiplyFront(new OpenMatrix3f(other));
    }

    public void multiply(float ratio) {
        m00 *= ratio;
        m01 *= ratio;
        m02 *= ratio;
        m10 *= ratio;
        m11 *= ratio;
        m12 *= ratio;
        m20 *= ratio;
        m21 *= ratio;
        m22 *= ratio;
    }

    public void load(FloatBuffer buffer) {
        m00 = buffer.get(bufferIndex(0, 0));
        m01 = buffer.get(bufferIndex(0, 1));
        m02 = buffer.get(bufferIndex(0, 2));
        m10 = buffer.get(bufferIndex(1, 0));
        m11 = buffer.get(bufferIndex(1, 1));
        m12 = buffer.get(bufferIndex(1, 2));
        m20 = buffer.get(bufferIndex(2, 0));
        m21 = buffer.get(bufferIndex(2, 1));
        m22 = buffer.get(bufferIndex(2, 2));
    }

    public void store(FloatBuffer buffer) {
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
    }

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }

    private static void multiply(OpenMatrix3f lhs, OpenMatrix3f rhs, OpenMatrix3f ret) {
        float m00 = lhs.m00 * rhs.m00 + lhs.m10 * rhs.m01 + lhs.m20 * rhs.m02;
        float m01 = lhs.m01 * rhs.m00 + lhs.m11 * rhs.m01 + lhs.m21 * rhs.m02;
        float m02 = lhs.m02 * rhs.m00 + lhs.m12 * rhs.m01 + lhs.m22 * rhs.m02;
        float m10 = lhs.m00 * rhs.m10 + lhs.m10 * rhs.m11 + lhs.m20 * rhs.m12;
        float m11 = lhs.m01 * rhs.m10 + lhs.m11 * rhs.m11 + lhs.m21 * rhs.m12;
        float m12 = lhs.m02 * rhs.m10 + lhs.m12 * rhs.m11 + lhs.m22 * rhs.m12;
        float m20 = lhs.m00 * rhs.m20 + lhs.m10 * rhs.m21 + lhs.m20 * rhs.m22;
        float m21 = lhs.m01 * rhs.m20 + lhs.m11 * rhs.m21 + lhs.m21 * rhs.m22;
        float m22 = lhs.m02 * rhs.m20 + lhs.m12 * rhs.m21 + lhs.m22 * rhs.m22;
        ret.m00 = m00;
        ret.m01 = m01;
        ret.m02 = m02;
        ret.m10 = m10;
        ret.m11 = m11;
        ret.m12 = m12;
        ret.m20 = m20;
        ret.m21 = m21;
        ret.m22 = m22;
    }
}
