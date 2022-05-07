package moe.plushie.armourers_workshop.utils.extened;

import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

// copy from `net.minecraft.util.math.vector.Matrix4f`
// we don't want to do this, but it has too many client side only methods.
@SuppressWarnings("unused")
public class AWMatrix4f {

    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;

    public AWMatrix4f() {
    }

    public AWMatrix4f(AWMatrix4f other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m03 = other.m03;
        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        this.m13 = other.m13;
        this.m20 = other.m20;
        this.m21 = other.m21;
        this.m22 = other.m22;
        this.m23 = other.m23;
        this.m30 = other.m30;
        this.m31 = other.m31;
        this.m32 = other.m32;
        this.m33 = other.m33;
    }

    public AWMatrix4f(Quaternion quat) {
        float f = quat.i();
        float f1 = quat.j();
        float f2 = quat.k();
        float f3 = quat.r();
        float f4 = 2.0F * f * f;
        float f5 = 2.0F * f1 * f1;
        float f6 = 2.0F * f2 * f2;
        this.m00 = 1.0F - f5 - f6;
        this.m11 = 1.0F - f6 - f4;
        this.m22 = 1.0F - f4 - f5;
        this.m33 = 1.0F;
        float f7 = f * f1;
        float f8 = f1 * f2;
        float f9 = f2 * f;
        float f10 = f * f3;
        float f11 = f1 * f3;
        float f12 = f2 * f3;
        this.m10 = 2.0F * (f7 + f12);
        this.m01 = 2.0F * (f7 - f12);
        this.m20 = 2.0F * (f9 - f11);
        this.m02 = 2.0F * (f9 + f11);
        this.m21 = 2.0F * (f8 + f10);
        this.m12 = 2.0F * (f8 - f10);
    }

    public AWMatrix4f(float[] values) {
        m00 = values[0];
        m01 = values[1];
        m02 = values[2];
        m03 = values[3];
        m10 = values[4];
        m11 = values[5];
        m12 = values[6];
        m13 = values[7];
        m20 = values[8];
        m21 = values[9];
        m22 = values[10];
        m23 = values[11];
        m30 = values[12];
        m31 = values[13];
        m32 = values[14];
        m33 = values[15];
    }

    public static AWMatrix4f createScaleMatrix(float x, float y, float z) {
        AWMatrix4f matrix = new AWMatrix4f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        matrix.m33 = 1.0F;
        return matrix;
    }

    public static AWMatrix4f createTranslateMatrix(float x, float y, float z) {
        AWMatrix4f matrix = new AWMatrix4f();
        matrix.m00 = 1.0F;
        matrix.m11 = 1.0F;
        matrix.m22 = 1.0F;
        matrix.m33 = 1.0F;
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        return matrix;
    }

    public void setIdentity() {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public float adjugateAndDet() {
        float f = this.m00 * this.m11 - this.m01 * this.m10;
        float f1 = this.m00 * this.m12 - this.m02 * this.m10;
        float f2 = this.m00 * this.m13 - this.m03 * this.m10;
        float f3 = this.m01 * this.m12 - this.m02 * this.m11;
        float f4 = this.m01 * this.m13 - this.m03 * this.m11;
        float f5 = this.m02 * this.m13 - this.m03 * this.m12;
        float f6 = this.m20 * this.m31 - this.m21 * this.m30;
        float f7 = this.m20 * this.m32 - this.m22 * this.m30;
        float f8 = this.m20 * this.m33 - this.m23 * this.m30;
        float f9 = this.m21 * this.m32 - this.m22 * this.m31;
        float f10 = this.m21 * this.m33 - this.m23 * this.m31;
        float f11 = this.m22 * this.m33 - this.m23 * this.m32;
        float f12 = this.m11 * f11 - this.m12 * f10 + this.m13 * f9;
        float f13 = -this.m10 * f11 + this.m12 * f8 - this.m13 * f7;
        float f14 = this.m10 * f10 - this.m11 * f8 + this.m13 * f6;
        float f15 = -this.m10 * f9 + this.m11 * f7 - this.m12 * f6;
        float f16 = -this.m01 * f11 + this.m02 * f10 - this.m03 * f9;
        float f17 = this.m00 * f11 - this.m02 * f8 + this.m03 * f7;
        float f18 = -this.m00 * f10 + this.m01 * f8 - this.m03 * f6;
        float f19 = this.m00 * f9 - this.m01 * f7 + this.m02 * f6;
        float f20 = this.m31 * f5 - this.m32 * f4 + this.m33 * f3;
        float f21 = -this.m30 * f5 + this.m32 * f2 - this.m33 * f1;
        float f22 = this.m30 * f4 - this.m31 * f2 + this.m33 * f;
        float f23 = -this.m30 * f3 + this.m31 * f1 - this.m32 * f;
        float f24 = -this.m21 * f5 + this.m22 * f4 - this.m23 * f3;
        float f25 = this.m20 * f5 - this.m22 * f2 + this.m23 * f1;
        float f26 = -this.m20 * f4 + this.m21 * f2 - this.m23 * f;
        float f27 = this.m20 * f3 - this.m21 * f1 + this.m22 * f;
        this.m00 = f12;
        this.m10 = f13;
        this.m20 = f14;
        this.m30 = f15;
        this.m01 = f16;
        this.m11 = f17;
        this.m21 = f18;
        this.m31 = f19;
        this.m02 = f20;
        this.m12 = f21;
        this.m22 = f22;
        this.m32 = f23;
        this.m03 = f24;
        this.m13 = f25;
        this.m23 = f26;
        this.m33 = f27;
        return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
    }

    public void transpose() {
        float f = this.m10;
        this.m10 = this.m01;
        this.m01 = f;
        f = this.m20;
        this.m20 = this.m02;
        this.m02 = f;
        f = this.m21;
        this.m21 = this.m12;
        this.m12 = f;
        f = this.m30;
        this.m30 = this.m03;
        this.m03 = f;
        f = this.m31;
        this.m31 = this.m13;
        this.m13 = f;
        f = this.m32;
        this.m32 = this.m23;
        this.m23 = f;
    }

    public boolean invert() {
        float f = this.adjugateAndDet();
        if (Math.abs(f) > 1.0E-6F) {
            this.multiply(f);
            return true;
        } else {
            return false;
        }
    }

    public void multiply(AWMatrix4f other) {
        float f = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
        float f1 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
        float f2 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
        float f3 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
        float f4 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
        float f5 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
        float f6 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
        float f7 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
        float f8 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
        float f9 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
        float f10 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
        float f11 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
        float f12 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;
        float f13 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;
        float f14 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;
        float f15 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;
        this.m00 = f;
        this.m01 = f1;
        this.m02 = f2;
        this.m03 = f3;
        this.m10 = f4;
        this.m11 = f5;
        this.m12 = f6;
        this.m13 = f7;
        this.m20 = f8;
        this.m21 = f9;
        this.m22 = f10;
        this.m23 = f11;
        this.m30 = f12;
        this.m31 = f13;
        this.m32 = f14;
        this.m33 = f15;
    }

    public void multiply(Quaternion quat) {
        this.multiply(new AWMatrix4f(quat));
    }

    public void multiply(float ratio) {
        this.m00 *= ratio;
        this.m01 *= ratio;
        this.m02 *= ratio;
        this.m03 *= ratio;
        this.m10 *= ratio;
        this.m11 *= ratio;
        this.m12 *= ratio;
        this.m13 *= ratio;
        this.m20 *= ratio;
        this.m21 *= ratio;
        this.m22 *= ratio;
        this.m23 *= ratio;
        this.m30 *= ratio;
        this.m31 *= ratio;
        this.m32 *= ratio;
        this.m33 *= ratio;
    }

    public static AWMatrix4f perspective(double p_195876_0_, float p_195876_2_, float p_195876_3_, float p_195876_4_) {
        float f = (float) (1.0D / Math.tan(p_195876_0_ * (double) ((float) Math.PI / 180F) / 2.0D));
        AWMatrix4f matrix = new AWMatrix4f();
        matrix.m00 = f / p_195876_2_;
        matrix.m11 = f;
        matrix.m22 = (p_195876_4_ + p_195876_3_) / (p_195876_3_ - p_195876_4_);
        matrix.m32 = -1.0F;
        matrix.m23 = 2.0F * p_195876_4_ * p_195876_3_ / (p_195876_3_ - p_195876_4_);
        return matrix;
    }

    public static AWMatrix4f orthographic(float p_195877_0_, float p_195877_1_, float p_195877_2_, float p_195877_3_) {
        AWMatrix4f matrix = new AWMatrix4f();
        matrix.m00 = 2.0F / p_195877_0_;
        matrix.m11 = 2.0F / p_195877_1_;
        float f = p_195877_3_ - p_195877_2_;
        matrix.m22 = -2.0F / f;
        matrix.m33 = 1.0F;
        matrix.m03 = -1.0F;
        matrix.m13 = -1.0F;
        matrix.m23 = -(p_195877_3_ + p_195877_2_) / f;
        return matrix;
    }

    public void translate(Vector3f p_226597_1_) {
        this.m03 += p_226597_1_.x();
        this.m13 += p_226597_1_.y();
        this.m23 += p_226597_1_.z();
    }

    public AWMatrix4f copy() {
        return new AWMatrix4f(this);
    }

    public void set(AWMatrix4f mat) {
        this.m00 = mat.m00;
        this.m01 = mat.m01;
        this.m02 = mat.m02;
        this.m03 = mat.m03;
        this.m10 = mat.m10;
        this.m11 = mat.m11;
        this.m12 = mat.m12;
        this.m13 = mat.m13;
        this.m20 = mat.m20;
        this.m21 = mat.m21;
        this.m22 = mat.m22;
        this.m23 = mat.m23;
        this.m30 = mat.m30;
        this.m31 = mat.m31;
        this.m32 = mat.m32;
        this.m33 = mat.m33;
    }

    public void add(AWMatrix4f other) {
        m00 += other.m00;
        m01 += other.m01;
        m02 += other.m02;
        m03 += other.m03;
        m10 += other.m10;
        m11 += other.m11;
        m12 += other.m12;
        m13 += other.m13;
        m20 += other.m20;
        m21 += other.m21;
        m22 += other.m22;
        m23 += other.m23;
        m30 += other.m30;
        m31 += other.m31;
        m32 += other.m32;
        m33 += other.m33;
    }

    public void multiplyBackward(AWMatrix4f other) {
        AWMatrix4f copy = other.copy();
        copy.multiply(this);
        this.set(copy);
    }

    public void setTranslation(float x, float y, float z) {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
        this.m33 = 1.0F;
        this.m03 = x;
        this.m13 = y;
        this.m23 = z;
    }

    public Matrix4f unwrap() {
        float[] values = {m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33,};
        return new Matrix4f(values);
    }
}
