package moe.plushie.armourers_workshop.utils.extened;

import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;

// copy from `net.minecraft.util.math.vector.Matrix3f`
// because `net.minecraft.util.math.vector.Matrix3f` has too many client side only methods.

@SuppressWarnings("unused")
public class AWMatrix3f {
    
    private static final float G = 3.0F + 2.0F * (float)Math.sqrt(2.0D);
    private static final float CS = (float)Math.cos((Math.PI / 8D));
    private static final float SS = (float)Math.sin((Math.PI / 8D));
    private static final float SQ2 = 1.0F / (float)Math.sqrt(2.0D);
    
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public AWMatrix3f() {
    }

    public AWMatrix3f(Quaternion quat) {
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

    public AWMatrix3f(AWMatrix4f other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        this.m20 = other.m20;
        this.m21 = other.m21;
        this.m22 = other.m22;
    }

    public AWMatrix3f(AWMatrix3f other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        this.m20 = other.m20;
        this.m21 = other.m21;
        this.m22 = other.m22;
    }

    public static AWMatrix3f createScaleMatrix(float x, float y, float z) {
        AWMatrix3f matrix = new AWMatrix3f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        return matrix;
    }

    public void transpose() {
        float f = this.m01;
        this.m01 = this.m10;
        this.m10 = f;
        f = this.m02;
        this.m02 = this.m20;
        this.m20 = f;
        f = this.m12;
        this.m12 = this.m21;
        this.m21 = f;
    }

    public void setIdentity() {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
    }

    public float adjugateAndDet() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float f1 = -(this.m10 * this.m22 - this.m12 * this.m20);
        float f2 = this.m10 * this.m21 - this.m11 * this.m20;
        float f3 = -(this.m01 * this.m22 - this.m02 * this.m21);
        float f4 = this.m00 * this.m22 - this.m02 * this.m20;
        float f5 = -(this.m00 * this.m21 - this.m01 * this.m20);
        float f6 = this.m01 * this.m12 - this.m02 * this.m11;
        float f7 = -(this.m00 * this.m12 - this.m02 * this.m10);
        float f8 = this.m00 * this.m11 - this.m01 * this.m10;
        float f9 = this.m00 * f + this.m01 * f1 + this.m02 * f2;
        this.m00 = f;
        this.m10 = f1;
        this.m20 = f2;
        this.m01 = f3;
        this.m11 = f4;
        this.m21 = f5;
        this.m02 = f6;
        this.m12 = f7;
        this.m22 = f8;
        return f9;
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

    public void set(int p_232605_1_, int p_232605_2_, float p_232605_3_) {
        if (p_232605_1_ == 0) {
            if (p_232605_2_ == 0) {
                this.m00 = p_232605_3_;
            } else if (p_232605_2_ == 1) {
                this.m01 = p_232605_3_;
            } else {
                this.m02 = p_232605_3_;
            }
        } else if (p_232605_1_ == 1) {
            if (p_232605_2_ == 0) {
                this.m10 = p_232605_3_;
            } else if (p_232605_2_ == 1) {
                this.m11 = p_232605_3_;
            } else {
                this.m12 = p_232605_3_;
            }
        } else if (p_232605_2_ == 0) {
            this.m20 = p_232605_3_;
        } else if (p_232605_2_ == 1) {
            this.m21 = p_232605_3_;
        } else {
            this.m22 = p_232605_3_;
        }

    }

    public void multiply(AWMatrix3f other) {
        float f = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20;
        float f1 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21;
        float f2 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22;
        float f3 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20;
        float f4 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21;
        float f5 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22;
        float f6 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20;
        float f7 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21;
        float f8 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22;
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
        this.multiply(new AWMatrix3f(other));
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

    public AWMatrix3f copy() {
        return new AWMatrix3f(this);
    }

    public Matrix4f upcasting() {
        float[] values = {m00, m01, m02, 0, m10, m11, m12, 0, m20, m21, m22, 0, 0, 0, 0, 0,};
        return new Matrix4f(values);
    }

    public Matrix3f unwrap() {
        return new Matrix3f(upcasting());
    }
}
