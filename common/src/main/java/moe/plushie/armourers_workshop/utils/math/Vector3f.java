package moe.plushie.armourers_workshop.utils.math;

import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenMatrix3f;
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

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Position pos) {
        this((float) pos.x(), (float) pos.y(), (float) pos.z());
    }

    public Vector3f(float[] values) {
        set(values);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other != null && this.getClass() == other.getClass()) {
            Vector3f vector3f = (Vector3f) other;
            if (Float.compare(vector3f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(vector3f.y, this.y) != 0) {
                return false;
            } else {
                return Float.compare(vector3f.z, this.z) == 0;
            }
        }
        return false;
    }

    @Override
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

    public void mul(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public void clamp(float minValue, float maxValue) {
        this.x = MathUtils.clamp(this.x, minValue, maxValue);
        this.y = MathUtils.clamp(this.y, minValue, maxValue);
        this.z = MathUtils.clamp(this.z, minValue, maxValue);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(Vector3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void sub(Vector3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public float dot(Vector3f vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
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

    public void cross(Vector3f vec) {
        float f = this.x;
        float f1 = this.y;
        float f2 = this.z;
        float f3 = vec.getX();
        float f4 = vec.getY();
        float f5 = vec.getZ();
        this.x = f1 * f5 - f2 * f4;
        this.y = f2 * f3 - f * f5;
        this.z = f * f4 - f1 * f3;
    }

    public void transform(Matrix3f mat) {
        float f = this.x;
        float f1 = this.y;
        float f2 = this.z;
        float[][] buf = OpenMatrix3f.toFloatBuffer(mat);
        this.x = buf[0][0] * f + buf[0][1] * f1 + buf[0][2] * f2;
        this.y = buf[1][0] * f + buf[1][1] * f1 + buf[1][2] * f2;
        this.z = buf[2][0] * f + buf[2][1] * f1 + buf[2][2] * f2;
    }

    public void transform(Quaternion value) {
        Quaternion quaternion = new Quaternion(value);
        quaternion.mul(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
        Quaternion quaternion1 = new Quaternion(value);
        quaternion1.conj();
        quaternion.mul(quaternion1);
        this.set(quaternion.i(), quaternion.j(), quaternion.k());
    }

    public void lerp(Vector3f vec, float f) {
        float f1 = 1.0F - f;
        this.x = this.x * f1 + vec.x * f;
        this.y = this.y * f1 + vec.y * f;
        this.z = this.z * f1 + vec.z * f;
    }

    public Quaternion rotation(float f) {
        return TrigUtils.rotate(this, f, false);
    }

    public Quaternion rotationDegrees(float f) {
        return TrigUtils.rotate(this, f, true);
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public void map(Float2FloatFunction transform) {
        this.x = transform.get(this.x);
        this.y = transform.get(this.y);
        this.z = transform.get(this.z);
    }

    @Override
    public String toString() {
        return String.format("(%g %g %g)", x, y, z);
    }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }
}

