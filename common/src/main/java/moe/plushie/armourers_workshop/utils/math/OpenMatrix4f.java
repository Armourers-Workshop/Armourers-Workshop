package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class OpenMatrix4f implements IMatrix4f {

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public OpenMatrix4f() {
    }

    public OpenMatrix4f(IMatrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.store(buffer);
        load(buffer);
    }

    public OpenMatrix4f(IQuaternionf quaternion) {
        float f = quaternion.x();
        float g = quaternion.y();
        float h = quaternion.z();
        float i = quaternion.w();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        m00 = 1.0f - k - l;
        m11 = 1.0f - l - j;
        m22 = 1.0f - j - k;
        m33 = 1.0f;
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

    public static OpenMatrix4f createScaleMatrix(float x, float y, float z) {
        OpenMatrix4f matrix = new OpenMatrix4f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        matrix.m33 = 1;
        return matrix;
    }

    public static OpenMatrix4f createTranslateMatrix(float x, float y, float z) {
        OpenMatrix4f matrix = new OpenMatrix4f();
        matrix.m00 = 1;
        matrix.m11 = 1;
        matrix.m22 = 1;
        matrix.m33 = 1;
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        return matrix;
    }

    public static OpenMatrix4f of(IMatrix4f o) {
        if (o instanceof OpenMatrix4f) {
            return (OpenMatrix4f) o;
        }
        return new OpenMatrix4f(o);
    }

    @Override
    public void scale(float x, float y, float z) {
        multiply(OpenMatrix4f.createScaleMatrix(x, y, z));
    }

    @Override
    public void translate(float x, float y, float z) {
        multiply(OpenMatrix4f.createTranslateMatrix(x, y, z));
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        multiply(new OpenMatrix4f(quaternion));
    }

    @Override
    public void multiply(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float w = values[3];
        values[0] = m00 * x + m01 * y + m02 * z + m03 * w;
        values[1] = m10 * x + m11 * y + m12 * z + m13 * w;
        values[2] = m20 * x + m21 * y + m22 * z + m23 * w;
        values[3] = m30 * x + m31 * y + m32 * z + m33 * w;
    }

    @Override
    public void multiply(IMatrix4f other) {
        multiply(of(other), this, this);
    }

    public void multiplyFront(IMatrix4f other) {
        multiply(this, of(other), this);
    }

    public void multiplyFront(IQuaternionf quaternion) {
        multiplyFront(new OpenMatrix4f(quaternion));
    }

    public void multiply(float f) {
        m00 *= f;
        m01 *= f;
        m02 *= f;
        m03 *= f;
        m10 *= f;
        m11 *= f;
        m12 *= f;
        m13 *= f;
        m20 *= f;
        m21 *= f;
        m22 *= f;
        m23 *= f;
        m30 *= f;
        m31 *= f;
        m32 *= f;
        m33 *= f;
    }

    @Override
    public void load(FloatBuffer buffer) {
        m00 = buffer.get(bufferIndex(0, 0));
        m01 = buffer.get(bufferIndex(0, 1));
        m02 = buffer.get(bufferIndex(0, 2));
        m03 = buffer.get(bufferIndex(0, 3));
        m10 = buffer.get(bufferIndex(1, 0));
        m11 = buffer.get(bufferIndex(1, 1));
        m12 = buffer.get(bufferIndex(1, 2));
        m13 = buffer.get(bufferIndex(1, 3));
        m20 = buffer.get(bufferIndex(2, 0));
        m21 = buffer.get(bufferIndex(2, 1));
        m22 = buffer.get(bufferIndex(2, 2));
        m23 = buffer.get(bufferIndex(2, 3));
        m30 = buffer.get(bufferIndex(3, 0));
        m31 = buffer.get(bufferIndex(3, 1));
        m32 = buffer.get(bufferIndex(3, 2));
        m33 = buffer.get(bufferIndex(3, 3));
    }

    @Override
    public void store(FloatBuffer buffer) {
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(0, 3), m03);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(1, 3), m13);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
        buffer.put(bufferIndex(2, 3), m23);
        buffer.put(bufferIndex(3, 0), m30);
        buffer.put(bufferIndex(3, 1), m31);
        buffer.put(bufferIndex(3, 2), m32);
        buffer.put(bufferIndex(3, 3), m33);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenMatrix4f:\n");
        builder.append(m00);
        builder.append(" ");
        builder.append(m01);
        builder.append(" ");
        builder.append(m02);
        builder.append(" ");
        builder.append(m03);
        builder.append("\n");
        builder.append(m10);
        builder.append(" ");
        builder.append(m11);
        builder.append(" ");
        builder.append(m12);
        builder.append(" ");
        builder.append(m13);
        builder.append("\n");
        builder.append(m20);
        builder.append(" ");
        builder.append(m21);
        builder.append(" ");
        builder.append(m22);
        builder.append(" ");
        builder.append(m23);
        builder.append("\n");
        builder.append(m30);
        builder.append(" ");
        builder.append(m31);
        builder.append(" ");
        builder.append(m32);
        builder.append(" ");
        builder.append(m33);
        builder.append("\n");
        return builder.toString();
    }

    public OpenMatrix4f set(OpenMatrix3f m) {
        m00 = m.m00;
        m01 = m.m01;
        m02 = m.m02;
        m03 = 0.0f;
        m10 = m.m10;
        m11 = m.m11;
        m12 = m.m12;
        m13 = 0.0f;
        m20 = m.m20;
        m21 = m.m21;
        m22 = m.m22;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
        return this;
    }

    public OpenMatrix4f set(OpenMatrix4f mat) {
        m00 = mat.m00;
        m01 = mat.m01;
        m02 = mat.m02;
        m03 = mat.m03;
        m10 = mat.m10;
        m11 = mat.m11;
        m12 = mat.m12;
        m13 = mat.m13;
        m20 = mat.m20;
        m21 = mat.m21;
        m22 = mat.m22;
        m23 = mat.m23;
        m30 = mat.m30;
        m31 = mat.m31;
        m32 = mat.m32;
        m33 = mat.m33;
        return this;
    }

    public OpenMatrix4f setIdentity() {
        m00 = 1.0f;
        m01 = 0.0f;
        m02 = 0.0f;
        m03 = 0.0f;
        m10 = 0.0f;
        m11 = 1.0f;
        m12 = 0.0f;
        m13 = 0.0f;
        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 1.0f;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
        return this;
    }

    public OpenMatrix4f copy() {
        return new OpenMatrix4f(this);
    }

    public float adjugateAndDet() {
        float f = m00 * m11 - m01 * m10;
        float g = m00 * m12 - m02 * m10;
        float h = m00 * m13 - m03 * m10;
        float i = m01 * m12 - m02 * m11;
        float j = m01 * m13 - m03 * m11;
        float k = m02 * m13 - m03 * m12;
        float l = m20 * m31 - m21 * m30;
        float m = m20 * m32 - m22 * m30;
        float n = m20 * m33 - m23 * m30;
        float o = m21 * m32 - m22 * m31;
        float p = m21 * m33 - m23 * m31;
        float q = m22 * m33 - m23 * m32;
        float r = m11 * q - m12 * p + m13 * o;
        float s = -m10 * q + m12 * n - m13 * m;
        float t = m10 * p - m11 * n + m13 * l;
        float u = -m10 * o + m11 * m - m12 * l;
        float v = -m01 * q + m02 * p - m03 * o;
        float w = m00 * q - m02 * n + m03 * m;
        float x = -m00 * p + m01 * n - m03 * l;
        float y = m00 * o - m01 * m + m02 * l;
        float z = m31 * k - m32 * j + m33 * i;
        float aa = -m30 * k + m32 * h - m33 * g;
        float ab = m30 * j - m31 * h + m33 * f;
        float ac = -m30 * i + m31 * g - m32 * f;
        float ad = -m21 * k + m22 * j - m23 * i;
        float ae = m20 * k - m22 * h + m23 * g;
        float af = -m20 * j + m21 * h - m23 * f;
        float ag = m20 * i - m21 * g + m22 * f;
        m00 = r;
        m10 = s;
        m20 = t;
        m30 = u;
        m01 = v;
        m11 = w;
        m21 = x;
        m31 = y;
        m02 = z;
        m12 = aa;
        m22 = ab;
        m32 = ac;
        m03 = ad;
        m13 = ae;
        m23 = af;
        m33 = ag;
        return f * q - g * p + h * o + i * n - j * m + k * l;
    }

    public float determinant() {
        float f = m00 * m11 - m01 * m10;
        float g = m00 * m12 - m02 * m10;
        float h = m00 * m13 - m03 * m10;
        float i = m01 * m12 - m02 * m11;
        float j = m01 * m13 - m03 * m11;
        float k = m02 * m13 - m03 * m12;
        float l = m20 * m31 - m21 * m30;
        float m = m20 * m32 - m22 * m30;
        float n = m20 * m33 - m23 * m30;
        float o = m21 * m32 - m22 * m31;
        float p = m21 * m33 - m23 * m31;
        float q = m22 * m33 - m23 * m32;
        return f * q - g * p + h * o + i * n - j * m + k * l;
    }

    public void transpose() {
        float f = m10;
        m10 = m01;
        m01 = f;
        f = m20;
        m20 = m02;
        m02 = f;
        f = m21;
        m21 = m12;
        m12 = f;
        f = m30;
        m30 = m03;
        m03 = f;
        f = m31;
        m31 = m13;
        m13 = f;
        f = m32;
        m32 = m23;
        m23 = f;
    }

    @Override
    public void invert() {
        float f = adjugateAndDet();
        if (Math.abs(f) > 1.0E-6f) {
            multiply(f);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenMatrix4f)) return false;
        OpenMatrix4f that = (OpenMatrix4f) o;
        if (Float.compare(that.m00, m00) != 0) return false;
        if (Float.compare(that.m01, m01) != 0) return false;
        if (Float.compare(that.m02, m02) != 0) return false;
        if (Float.compare(that.m03, m03) != 0) return false;
        if (Float.compare(that.m10, m10) != 0) return false;
        if (Float.compare(that.m11, m11) != 0) return false;
        if (Float.compare(that.m12, m12) != 0) return false;
        if (Float.compare(that.m13, m13) != 0) return false;
        if (Float.compare(that.m20, m20) != 0) return false;
        if (Float.compare(that.m21, m21) != 0) return false;
        if (Float.compare(that.m22, m22) != 0) return false;
        if (Float.compare(that.m23, m23) != 0) return false;
        if (Float.compare(that.m30, m30) != 0) return false;
        if (Float.compare(that.m31, m31) != 0) return false;
        if (Float.compare(that.m32, m32) != 0) return false;
        return Float.compare(that.m33, m33) == 0;
    }

    @Override
    public int hashCode() {
        int result = (m00 != +0.0f ? Float.floatToIntBits(m00) : 0);
        result = 31 * result + (m01 != +0.0f ? Float.floatToIntBits(m01) : 0);
        result = 31 * result + (m02 != +0.0f ? Float.floatToIntBits(m02) : 0);
        result = 31 * result + (m03 != +0.0f ? Float.floatToIntBits(m03) : 0);
        result = 31 * result + (m10 != +0.0f ? Float.floatToIntBits(m10) : 0);
        result = 31 * result + (m11 != +0.0f ? Float.floatToIntBits(m11) : 0);
        result = 31 * result + (m12 != +0.0f ? Float.floatToIntBits(m12) : 0);
        result = 31 * result + (m13 != +0.0f ? Float.floatToIntBits(m13) : 0);
        result = 31 * result + (m20 != +0.0f ? Float.floatToIntBits(m20) : 0);
        result = 31 * result + (m21 != +0.0f ? Float.floatToIntBits(m21) : 0);
        result = 31 * result + (m22 != +0.0f ? Float.floatToIntBits(m22) : 0);
        result = 31 * result + (m23 != +0.0f ? Float.floatToIntBits(m23) : 0);
        result = 31 * result + (m30 != +0.0f ? Float.floatToIntBits(m30) : 0);
        result = 31 * result + (m31 != +0.0f ? Float.floatToIntBits(m31) : 0);
        result = 31 * result + (m32 != +0.0f ? Float.floatToIntBits(m32) : 0);
        result = 31 * result + (m33 != +0.0f ? Float.floatToIntBits(m33) : 0);
        return result;
    }

    private static int bufferIndex(int i, int j) {
        return j * 4 + i;
    }

    private static void multiply(OpenMatrix4f lhs, OpenMatrix4f rhs, OpenMatrix4f ret) {
        float m00 = lhs.m00 * rhs.m00 + lhs.m10 * rhs.m01 + lhs.m20 * rhs.m02 + lhs.m30 * rhs.m03;
        float m01 = lhs.m01 * rhs.m00 + lhs.m11 * rhs.m01 + lhs.m21 * rhs.m02 + lhs.m31 * rhs.m03;
        float m02 = lhs.m02 * rhs.m00 + lhs.m12 * rhs.m01 + lhs.m22 * rhs.m02 + lhs.m32 * rhs.m03;
        float m03 = lhs.m03 * rhs.m00 + lhs.m13 * rhs.m01 + lhs.m23 * rhs.m02 + lhs.m33 * rhs.m03;
        float m10 = lhs.m00 * rhs.m10 + lhs.m10 * rhs.m11 + lhs.m20 * rhs.m12 + lhs.m30 * rhs.m13;
        float m11 = lhs.m01 * rhs.m10 + lhs.m11 * rhs.m11 + lhs.m21 * rhs.m12 + lhs.m31 * rhs.m13;
        float m12 = lhs.m02 * rhs.m10 + lhs.m12 * rhs.m11 + lhs.m22 * rhs.m12 + lhs.m32 * rhs.m13;
        float m13 = lhs.m03 * rhs.m10 + lhs.m13 * rhs.m11 + lhs.m23 * rhs.m12 + lhs.m33 * rhs.m13;
        float m20 = lhs.m00 * rhs.m20 + lhs.m10 * rhs.m21 + lhs.m20 * rhs.m22 + lhs.m30 * rhs.m23;
        float m21 = lhs.m01 * rhs.m20 + lhs.m11 * rhs.m21 + lhs.m21 * rhs.m22 + lhs.m31 * rhs.m23;
        float m22 = lhs.m02 * rhs.m20 + lhs.m12 * rhs.m21 + lhs.m22 * rhs.m22 + lhs.m32 * rhs.m23;
        float m23 = lhs.m03 * rhs.m20 + lhs.m13 * rhs.m21 + lhs.m23 * rhs.m22 + lhs.m33 * rhs.m23;
        float m30 = lhs.m00 * rhs.m30 + lhs.m10 * rhs.m31 + lhs.m20 * rhs.m32 + lhs.m30 * rhs.m33;
        float m31 = lhs.m01 * rhs.m30 + lhs.m11 * rhs.m31 + lhs.m21 * rhs.m32 + lhs.m31 * rhs.m33;
        float m32 = lhs.m02 * rhs.m30 + lhs.m12 * rhs.m31 + lhs.m22 * rhs.m32 + lhs.m32 * rhs.m33;
        float m33 = lhs.m03 * rhs.m30 + lhs.m13 * rhs.m31 + lhs.m23 * rhs.m32 + lhs.m33 * rhs.m33;
        ret.m00 = m00;
        ret.m01 = m01;
        ret.m02 = m02;
        ret.m03 = m03;
        ret.m10 = m10;
        ret.m11 = m11;
        ret.m12 = m12;
        ret.m13 = m13;
        ret.m20 = m20;
        ret.m21 = m21;
        ret.m22 = m22;
        ret.m23 = m23;
        ret.m30 = m30;
        ret.m31 = m31;
        ret.m32 = m32;
        ret.m33 = m33;
    }
}
