package moe.plushie.armourers_workshop.utils.ext;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import java.nio.FloatBuffer;

public class OpenMatrix4f extends Matrix4f {

    public OpenMatrix4f() {
        super();
    }

    public OpenMatrix4f(Matrix4f matrix4f) {
        super(matrix4f);
    }

    public OpenMatrix4f(Quaternion quaternion) {
        super(quaternion);
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

    public static OpenMatrix4f of(Matrix4f matrix) {
        if (matrix instanceof OpenMatrix4f) {
            return (OpenMatrix4f) matrix;
        }
        return new OpenMatrix4f(matrix);
    }

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

    public void multiply(Matrix4f other) {
        OpenMatrix4f.multiply(this, OpenMatrix4f.of(other), this);
    }

    public void multiply(Quaternion quaternion) {
        multiply(new OpenMatrix4f(quaternion));
    }

    public void multiplyFront(Matrix4f other) {
        multiply(of(other), this, this);
    }

    public void multiplyFront(Quaternion other) {
        multiplyFront(new OpenMatrix4f(other));
    }

    public void multiply(float f) {
        this.m00 *= f;
        this.m01 *= f;
        this.m02 *= f;
        this.m03 *= f;
        this.m10 *= f;
        this.m11 *= f;
        this.m12 *= f;
        this.m13 *= f;
        this.m20 *= f;
        this.m21 *= f;
        this.m22 *= f;
        this.m23 *= f;
        this.m30 *= f;
        this.m31 *= f;
        this.m32 *= f;
        this.m33 *= f;
    }

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
