package moe.plushie.armourers_workshop.utils.ext;

import com.mojang.math.Matrix3f;
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

    public static OpenMatrix3f createScaleMatrix(float x, float y, float z) {
        OpenMatrix3f matrix = new OpenMatrix3f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        return matrix;
    }

    public static OpenMatrix3f fromFloatBuffer(float[][] values) {
        OpenMatrix3f matrix = new OpenMatrix3f();
        matrix.m00 = values[0][0];
        matrix.m01 = values[0][1];
        matrix.m02 = values[0][2];
        matrix.m10 = values[1][0];
        matrix.m11 = values[1][1];
        matrix.m12 = values[1][2];
        matrix.m20 = values[2][0];
        matrix.m21 = values[2][1];
        matrix.m22 = values[2][2];
        return matrix;
    }

    public static float[][] toFloatBuffer(Matrix3f matrix) {
        if (matrix instanceof OpenMatrix3f) {
            return ((OpenMatrix3f) matrix).toArray();
        }
        return new OpenMatrix3f(matrix).toArray();
    }

    public float[][] toArray() {
        return new float[][]{{m00, m01, m02}, {m10, m11, m12}, {m20, m21, m22}};
    }

    public void multiply(Matrix3f other) {
        float[][] buf = toFloatBuffer(other);
        float f = m00 * buf[0][0] + m01 * buf[1][0] + m02 * buf[2][0];
        float f1 = m00 * buf[0][1] + m01 * buf[1][1] + m02 * buf[2][1];
        float f2 = m00 * buf[0][2] + m01 * buf[1][2] + m02 * buf[2][2];
        float f3 = m10 * buf[0][0] + m11 * buf[1][0] + m12 * buf[2][0];
        float f4 = m10 * buf[0][1] + m11 * buf[1][1] + m12 * buf[2][1];
        float f5 = m10 * buf[0][2] + m11 * buf[1][2] + m12 * buf[2][2];
        float f6 = m20 * buf[0][0] + m21 * buf[1][0] + m22 * buf[2][0];
        float f7 = m20 * buf[0][1] + m21 * buf[1][1] + m22 * buf[2][1];
        float f8 = m20 * buf[0][2] + m21 * buf[1][2] + m22 * buf[2][2];
        this.m00 = f;
        this.m01 = f1;
        this.m02 = f2;
        this.m10 = f3;
        this.m11 = f4;
        this.m12 = f5;
        this.m20 = f6;
        this.m21 = f7;
        this.m22 = f8;
    }

    public void multiply(Quaternion other) {
        this.multiply(new OpenMatrix3f(other));
    }

    public void multiply(float ratio) {
        this.m00 *= ratio;
        this.m01 *= ratio;
        this.m02 *= ratio;
        this.m10 *= ratio;
        this.m11 *= ratio;
        this.m12 *= ratio;
        this.m20 *= ratio;
        this.m21 *= ratio;
        this.m22 *= ratio;
    }

    public void store(FloatBuffer floatBuffer) {
        floatBuffer.put(bufferIndex(0, 0), this.m00);
        floatBuffer.put(bufferIndex(0, 1), this.m01);
        floatBuffer.put(bufferIndex(0, 2), this.m02);
        floatBuffer.put(bufferIndex(1, 0), this.m10);
        floatBuffer.put(bufferIndex(1, 1), this.m11);
        floatBuffer.put(bufferIndex(1, 2), this.m12);
        floatBuffer.put(bufferIndex(2, 0), this.m20);
        floatBuffer.put(bufferIndex(2, 1), this.m21);
        floatBuffer.put(bufferIndex(2, 2), this.m22);
    }

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }
}
