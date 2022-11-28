package moe.plushie.armourers_workshop.utils.math;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenMatrix4f;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public class Vector4f {

    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Position pos) {
        this((float) pos.x(), (float) pos.y(), (float) pos.z(), 1.0F);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && this.getClass() == other.getClass()) {
            Vector4f vector4f = (Vector4f) other;
            if (Float.compare(vector4f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(vector4f.y, this.y) != 0) {
                return false;
            } else if (Float.compare(vector4f.z, this.z) != 0) {
                return false;
            } else {
                return Float.compare(vector4f.w, this.w) == 0;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return 31 * i + Float.floatToIntBits(this.w);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float w() {
        return this.w;
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

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void mul(Position pos) {
        this.x *= pos.x();
        this.y *= pos.y();
        this.z *= pos.z();
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float dot(Vector4f pos) {
        return this.x * pos.x + this.y * pos.y + this.z * pos.z + this.w * pos.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double) f < 1.0E-5D) {
            return false;
        } else {
            float f1 = MathUtils.fastInvSqrt(f);
            this.x *= f1;
            this.y *= f1;
            this.z *= f1;
            this.w *= f1;
            return true;
        }
    }

    public void transform(Matrix4f matrix) {
        float[] floats = {x, y, z, w};
        OpenMatrix4f.of(matrix).multiply(floats);
        x = floats[0];
        y = floats[1];
        z = floats[2];
        w = floats[3];
    }

    public void transform(Quaternion q) {
        Quaternion quaternion = new Quaternion(q);
        quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
        Quaternion quaternion1 = new Quaternion(q);
        quaternion1.conj();
        quaternion.mul(quaternion1);
        this.set(quaternion.i(), quaternion.j(), quaternion.k(), this.w());
    }

    public void perspectiveDivide() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0F;
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }
}

