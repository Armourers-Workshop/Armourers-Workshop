package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class OpenMatrix3f implements IMatrix3f {

    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    public OpenMatrix3f() {
    }

    public OpenMatrix3f(IMatrix3f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        matrix.get(buffer);
        set(buffer);
    }

    public OpenMatrix3f(IMatrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        m00 = buffer.get(bufferIndex4(0, 0));
        m01 = buffer.get(bufferIndex4(0, 1));
        m02 = buffer.get(bufferIndex4(0, 2));
        m10 = buffer.get(bufferIndex4(1, 0));
        m11 = buffer.get(bufferIndex4(1, 1));
        m12 = buffer.get(bufferIndex4(1, 2));
        m20 = buffer.get(bufferIndex4(2, 0));
        m21 = buffer.get(bufferIndex4(2, 1));
        m22 = buffer.get(bufferIndex4(2, 2));
    }

    public OpenMatrix3f(IQuaternionf quaternion) {
        float f = quaternion.i();
        float g = quaternion.j();
        float h = quaternion.k();
        float i = quaternion.r();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        m00 = 1.0f - k - l;
        m11 = 1.0f - l - j;
        m22 = 1.0f - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        m10 = 2.0f * (m + r);
        m01 = 2.0f * (m - r);
        m20 = 2.0f * (o - q);
        m02 = 2.0f * (o + q);
        m21 = 2.0f * (n + p);
        m12 = 2.0f * (n - p);
    }

    public static OpenMatrix3f createScaleMatrix(float x, float y, float z) {
        OpenMatrix3f matrix = new OpenMatrix3f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        return matrix;
    }

    public static OpenMatrix3f of(IMatrix3f o) {
        if (o instanceof OpenMatrix3f) {
            return (OpenMatrix3f) o;
        }
        return new OpenMatrix3f(o);
    }

    @Override
    public void multiply(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        values[0] = m00 * x + m01 * y + m02 * z;
        values[1] = m10 * x + m11 * y + m12 * z;
        values[2] = m20 * x + m21 * y + m22 * z;
    }

    @Override
    public void multiply(IMatrix3f other) {
        multiply(of(other), this, this);
    }

    @Override
    public void rotate(IQuaternionf other) {
        multiply(new OpenMatrix3f(other));
    }

    public void multiplyFront(IMatrix3f other) {
        multiply(this, of(other), this);
    }

    public void multiplyFront(IQuaternionf other) {
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

    public void set(FloatBuffer buffer) {
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

    public void get(FloatBuffer buffer) {
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenMatrix3f:\n");
        builder.append(m00);
        builder.append(" ");
        builder.append(m01);
        builder.append(" ");
        builder.append(m02);
        builder.append("\n");
        builder.append(m10);
        builder.append(" ");
        builder.append(m11);
        builder.append(" ");
        builder.append(m12);
        builder.append("\n");
        builder.append(m20);
        builder.append(" ");
        builder.append(m21);
        builder.append(" ");
        builder.append(m22);
        builder.append("\n");
        return builder.toString();
    }

    public OpenMatrix3f set(OpenMatrix3f m) {
        m00 = m.m00;
        m01 = m.m01;
        m02 = m.m02;
        m10 = m.m10;
        m11 = m.m11;
        m12 = m.m12;
        m20 = m.m20;
        m21 = m.m21;
        m22 = m.m22;
        return this;
    }

    public OpenMatrix3f set(OpenMatrix4f mat) {
        m00 = mat.m00;
        m01 = mat.m01;
        m02 = mat.m02;
        m10 = mat.m10;
        m11 = mat.m11;
        m12 = mat.m12;
        m20 = mat.m20;
        m21 = mat.m21;
        m22 = mat.m22;
        return this;
    }

    public OpenMatrix3f copy() {
        return new OpenMatrix3f(this);
    }


    public float adjugateAndDet() {
        float f = m11 * m22 - m12 * m21;
        float g = -(m10 * m22 - m12 * m20);
        float h = m10 * m21 - m11 * m20;
        float i = -(m01 * m22 - m02 * m21);
        float j = m00 * m22 - m02 * m20;
        float k = -(m00 * m21 - m01 * m20);
        float l = m01 * m12 - m02 * m11;
        float m = -(m00 * m12 - m02 * m10);
        float n = m00 * m11 - m01 * m10;
        float o = m00 * f + m01 * g + m02 * h;
        m00 = f;
        m10 = g;
        m20 = h;
        m01 = i;
        m11 = j;
        m21 = k;
        m02 = l;
        m12 = m;
        m22 = n;
        return o;
    }

    public float determinant() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float g = -(this.m10 * this.m22 - this.m12 * this.m20);
        float h = this.m10 * this.m21 - this.m11 * this.m20;
        return this.m00 * f + this.m01 * g + this.m02 * h;
    }

    @Override
    public void invert() {
        float f = adjugateAndDet();
        if (Math.abs(f) > 1.0E-6f) {
            multiply(f);
        }
    }

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }

    private static int bufferIndex4(int i, int j) {
        return j * 4 + i;
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
