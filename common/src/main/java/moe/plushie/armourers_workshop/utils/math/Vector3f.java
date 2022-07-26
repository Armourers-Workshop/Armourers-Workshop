package moe.plushie.armourers_workshop.utils.math;

import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ext.ExtendedMatrix3f;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public final class Vector3f implements Position {

    public static Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
    public static Vector3f ONE = new Vector3f(1.0f, 1.0F, 1.0F);

    public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
    public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
    public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
    public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
    public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
    public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);

    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(float p_i48098_1_, float p_i48098_2_, float p_i48098_3_) {
        this.x = p_i48098_1_;
        this.y = p_i48098_2_;
        this.z = p_i48098_3_;
    }

    public Vector3f(Vector3d p_i51412_1_) {
        this((float) p_i51412_1_.x, (float) p_i51412_1_.y, (float) p_i51412_1_.z);
    }

    // Forge start
    public Vector3f(float[] values) {
        set(values);
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            Vector3f vector3f = (Vector3f) p_equals_1_;
            if (Float.compare(vector3f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(vector3f.y, this.y) != 0) {
                return false;
            } else {
                return Float.compare(vector3f.z, this.z) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        return 31 * i + Float.floatToIntBits(this.z);
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void mul(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public void mul(float p_229192_1_, float p_229192_2_, float p_229192_3_) {
        this.x *= p_229192_1_;
        this.y *= p_229192_2_;
        this.z *= p_229192_3_;
    }

    public void clamp(float p_195901_1_, float p_195901_2_) {
        this.x = MathUtils.clamp(this.x, p_195901_1_, p_195901_2_);
        this.y = MathUtils.clamp(this.y, p_195901_1_, p_195901_2_);
        this.z = MathUtils.clamp(this.z, p_195901_1_, p_195901_2_);
    }

    public void set(float p_195905_1_, float p_195905_2_, float p_195905_3_) {
        this.x = p_195905_1_;
        this.y = p_195905_2_;
        this.z = p_195905_3_;
    }

    public void add(float p_195904_1_, float p_195904_2_, float p_195904_3_) {
        this.x += p_195904_1_;
        this.y += p_195904_2_;
        this.z += p_195904_3_;
    }

    public void add(Vector3f p_229189_1_) {
        this.x += p_229189_1_.x;
        this.y += p_229189_1_.y;
        this.z += p_229189_1_.z;
    }

    public void sub(Vector3f p_195897_1_) {
        this.x -= p_195897_1_.x;
        this.y -= p_195897_1_.y;
        this.z -= p_195897_1_.z;
    }

    public float dot(Vector3f p_195903_1_) {
        return this.x * p_195903_1_.x + this.y * p_195903_1_.y + this.z * p_195903_1_.z;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if ((double) f < 1.0E-5D) {
            return false;
        } else {
            float f1 = MathUtils.fastInvSqrt(f);
            this.x *= f1;
            this.y *= f1;
            this.z *= f1;
            return true;
        }
    }

    public void cross(Vector3f p_195896_1_) {
        float f = this.x;
        float f1 = this.y;
        float f2 = this.z;
        float f3 = p_195896_1_.getX();
        float f4 = p_195896_1_.getY();
        float f5 = p_195896_1_.getZ();
        this.x = f1 * f5 - f2 * f4;
        this.y = f2 * f3 - f * f5;
        this.z = f * f4 - f1 * f3;
    }

    public void transform(Matrix3f p_229188_1_) {
        float f = this.x;
        float f1 = this.y;
        float f2 = this.z;
        float[][] buf = ExtendedMatrix3f.toFloatBuffer(p_229188_1_);
        this.x = buf[0][0] * f + buf[0][1] * f1 + buf[0][2] * f2;
        this.y = buf[1][0] * f + buf[1][1] * f1 + buf[1][2] * f2;
        this.z = buf[2][0] * f + buf[2][1] * f1 + buf[2][2] * f2;
    }

    public void transform(Quaternion p_214905_1_) {
        Quaternion quaternion = new Quaternion(p_214905_1_);
        quaternion.mul(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
        Quaternion quaternion1 = new Quaternion(p_214905_1_);
        quaternion1.conj();
        quaternion.mul(quaternion1);
        this.set(quaternion.i(), quaternion.j(), quaternion.k());
    }

    public void lerp(Vector3f p_229190_1_, float p_229190_2_) {
        float f = 1.0F - p_229190_2_;
        this.x = this.x * f + p_229190_1_.x * p_229190_2_;
        this.y = this.y * f + p_229190_1_.y * p_229190_2_;
        this.z = this.z * f + p_229190_1_.z * p_229190_2_;
    }

    public Quaternion rotation(float p_229193_1_) {
        return new Quaternion(new com.mojang.math.Vector3f(x, y, z), p_229193_1_, false);
    }

    public Quaternion rotationDegrees(float p_229187_1_) {
        return new Quaternion(new com.mojang.math.Vector3f(x, y, z), p_229187_1_, true);
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public void map(Float2FloatFunction p_229191_1_) {
        this.x = p_229191_1_.get(this.x);
        this.y = p_229191_1_.get(this.y);
        this.z = p_229191_1_.get(this.z);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }
}

