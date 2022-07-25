package moe.plushie.armourers_workshop.utils.ext;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

public class MatrixX4f extends Matrix4f {

    public MatrixX4f() {
        super();
    }

    public MatrixX4f(Matrix4f matrix4f) {
        super(matrix4f);
    }

    public MatrixX4f(Quaternion quaternion) {
        super(quaternion);
    }

    public static MatrixX4f createScaleMatrix(float x, float y, float z) {
        MatrixX4f matrix = new MatrixX4f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        matrix.m33 = 1.0F;
        return matrix;
    }

    public static MatrixX4f createTranslateMatrix(float x, float y, float z) {
        MatrixX4f matrix = new MatrixX4f();
        matrix.m00 = 1.0F;
        matrix.m11 = 1.0F;
        matrix.m22 = 1.0F;
        matrix.m33 = 1.0F;
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        return matrix;
    }

    public static MatrixX4f fromFloatBuffer(float[][] values) {
        MatrixX4f matrix = new MatrixX4f();
        matrix.m00 = values[0][0];
        matrix.m01 = values[0][1];
        matrix.m02 = values[0][2];
        matrix.m03 = values[0][3];
        matrix.m10 = values[1][0];
        matrix.m11 = values[1][1];
        matrix.m12 = values[1][2];
        matrix.m13 = values[1][3];
        matrix.m20 = values[2][0];
        matrix.m21 = values[2][1];
        matrix.m22 = values[2][2];
        matrix.m23 = values[2][3];
        matrix.m30 = values[3][0];
        matrix.m31 = values[3][1];
        matrix.m32 = values[3][2];
        matrix.m33 = values[3][3];
        return matrix;
    }

    public static float[][] toFloatBuffer(Matrix4f matrix) {
        if (matrix instanceof MatrixX4f) {
            return ((MatrixX4f) matrix).toArray();
        }
        return new MatrixX4f(matrix).toArray();
    }

    public float[][] toArray() {
        return new float[][]{{m00, m01, m02, m03}, {m10, m11, m12, m13}, {m20, m21, m22, m23}, {m30, m31, m32, m33}};
    }

    public void multiply(Matrix4f other) {
        float[][] buf = toFloatBuffer(other);
        float f = m00 * buf[0][0] + m01 * buf[1][0] + m02 * buf[2][0] + m03 * buf[3][0];
        float g = m00 * buf[0][1] + m01 * buf[1][1] + m02 * buf[2][1] + m03 * buf[3][1];
        float h = m00 * buf[0][2] + m01 * buf[1][2] + m02 * buf[2][2] + m03 * buf[3][2];
        float i = m00 * buf[0][3] + m01 * buf[1][3] + m02 * buf[2][3] + m03 * buf[3][3];
        float j = m10 * buf[0][0] + m11 * buf[1][0] + m12 * buf[2][0] + m13 * buf[3][0];
        float k = m10 * buf[0][1] + m11 * buf[1][1] + m12 * buf[2][1] + m13 * buf[3][1];
        float l = m10 * buf[0][2] + m11 * buf[1][2] + m12 * buf[2][2] + m13 * buf[3][2];
        float m = m10 * buf[0][3] + m11 * buf[1][3] + m12 * buf[2][3] + m13 * buf[3][3];
        float n = m20 * buf[0][0] + m21 * buf[1][0] + m22 * buf[2][0] + m23 * buf[3][0];
        float o = m20 * buf[0][1] + m21 * buf[1][1] + m22 * buf[2][1] + m23 * buf[3][1];
        float p = m20 * buf[0][2] + m21 * buf[1][2] + m22 * buf[2][2] + m23 * buf[3][2];
        float q = m20 * buf[0][3] + m21 * buf[1][3] + m22 * buf[2][3] + m23 * buf[3][3];
        float r = m30 * buf[0][0] + m31 * buf[1][0] + m32 * buf[2][0] + m33 * buf[3][0];
        float s = m30 * buf[0][1] + m31 * buf[1][1] + m32 * buf[2][1] + m33 * buf[3][1];
        float t = m30 * buf[0][2] + m31 * buf[1][2] + m32 * buf[2][2] + m33 * buf[3][2];
        float u = m30 * buf[0][3] + m31 * buf[1][3] + m32 * buf[2][3] + m33 * buf[3][3];
        this.m00 = f;
        this.m01 = g;
        this.m02 = h;
        this.m03 = i;
        this.m10 = j;
        this.m11 = k;
        this.m12 = l;
        this.m13 = m;
        this.m20 = n;
        this.m21 = o;
        this.m22 = p;
        this.m23 = q;
        this.m30 = r;
        this.m31 = s;
        this.m32 = t;
        this.m33 = u;
    }

    public void multiply(Quaternion quaternion) {
        this.multiply(new MatrixX4f(quaternion));
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
}
