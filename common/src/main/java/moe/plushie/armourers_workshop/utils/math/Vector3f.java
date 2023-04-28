package moe.plushie.armourers_workshop.utils.math;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public final class Vector3f implements IVector3f, Position {

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

    public Vector3f(Vector3f pos) {
        this(pos.x, pos.y, pos.z);
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

    @Override
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
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
        float ax = this.x;
        float ay = this.y;
        float az = this.z;
        float bx = vec.getX();
        float by = vec.getY();
        float bz = vec.getZ();
        this.x = ay * bz - az * by;
        this.y = az * bx - ax * bz;
        this.z = ax * by - ay * bx;
    }

    public void transform(IMatrix3f mat) {
        float[] floats = {x, y, z};
        mat.multiply(floats);
        set(floats[0], floats[1], floats[2]);
    }

    public void transform(OpenQuaternionf value) {
        OpenQuaternionf quaternion = new OpenQuaternionf(value);
        quaternion.mul(new OpenQuaternionf(x, y, z, 0.0F));
        OpenQuaternionf quaternion1 = new OpenQuaternionf(value);
        quaternion1.conj();
        quaternion.mul(quaternion1);
        set(quaternion.x(), quaternion.y(), quaternion.z());
    }

    public void lerp(Vector3f vec, float f) {
        float f1 = 1.0F - f;
        this.x = this.x * f1 + vec.x * f;
        this.y = this.y * f1 + vec.y * f;
        this.z = this.z * f1 + vec.z * f;
    }

    /**
     * Computes distance of Vector3 vector to v.
     */
   public float distanceTo(Vector3f v) {
        return MathUtils.sqrt(distanceToSquared(v));
    }

    /**
     * Computes squared distance of Vector3 vector to v.
     */
    public float distanceToSquared(Vector3f v) {
        float dx = x - v.x;
        float dy = y - v.y;
        float dz = z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public OpenQuaternionf rotation(float f) {
        return new OpenQuaternionf(this, f, false);
    }

    public OpenQuaternionf rotationDegrees(float f) {
        return new OpenQuaternionf(this, f, true);
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

    public void set(Vector3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }

    public void setFromSpherical(Sphericalf s) {
        this.setFromSphericalCoords(s.radius, s.phi, s.theta);

    }

    public void setFromSphericalCoords(float radius, float phi, float theta) {
        float sinPhiRadius = MathUtils.sin(phi) * radius;
        this.x = sinPhiRadius * MathUtils.sin(theta);
        this.y = MathUtils.cos(phi) * radius;
        this.z = sinPhiRadius * MathUtils.cos(theta);
    }
}

