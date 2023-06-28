package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
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

    @Override
    public float getY() {
        return y;
    }
    @Override
    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3f pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }

    public void add(float tx, float ty, float tz) {
        x += tx;
        y += ty;
        z += tz;
    }

    public void add(Vector3f pos) {
        x += pos.x;
        y += pos.y;
        z += pos.z;
    }

    public void subtract(float tx, float ty, float tz) {
        x -= tx;
        y -= ty;
        z -= tz;
    }

    public void subtract(Vector3f pos) {
        x -= pos.x;
        y -= pos.y;
        z -= pos.z;
    }

    public void scale(float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    public void scale(float sx, float sy, float sz) {
        x *= sx;
        y *= sy;
        z *= sz;
    }

    public void transform(IMatrix3f mat) {
        float[] floats = {x, y, z};
        mat.multiply(floats);
        set(floats[0], floats[1], floats[2]);
    }

    public void transform(IMatrix4f mat) {
        float[] floats = {x, y, z, 1f};
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

    public void normalize() {
        float f = x * x + y * y + z * z;
        if (f < 1.0E-5D) {
            return;
        }
        float f1 = MathUtils.fastInvSqrt(f);
        this.x *= f1;
        this.y *= f1;
        this.z *= f1;
    }

    public void cross(Vector3f pos) {
        float ax = this.x;
        float ay = this.y;
        float az = this.z;
        float bx = pos.getX();
        float by = pos.getY();
        float bz = pos.getZ();
        this.x = ay * bz - az * by;
        this.y = az * bx - ax * bz;
        this.z = ax * by - ay * bx;
    }

    public void clamp(float minValue, float maxValue) {
        x = MathUtils.clamp(x, minValue, maxValue);
        y = MathUtils.clamp(y, minValue, maxValue);
        z = MathUtils.clamp(z, minValue, maxValue);
    }

    public void lerp(Vector3f pos, float f) {
        float f1 = 1.0F - f;
        this.x = this.x * f1 + pos.x * f;
        this.y = this.y * f1 + pos.y * f;
        this.z = this.z * f1 + pos.z * f;
    }

    public float dot(Vector3f pos) {
        return x * pos.x + y * pos.y + z * pos.z;
    }

    public float length() {
        return MathUtils.sqrt(x * x + y * y + z * z);
    }

    /**
     * Computes distance of Vector3 vector to pos.
     */
   public float distanceTo(Vector3f pos) {
        return MathUtils.sqrt(distanceToSquared(pos));
    }

    /**
     * Computes squared distance of Vector3 vector to v.
     */
    public float distanceToSquared(Vector3f pos) {
        float dx = x - pos.x;
        float dy = y - pos.y;
        float dz = z - pos.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public OpenQuaternionf rotation(float f) {
        return new OpenQuaternionf(this, f, false);
    }

    public OpenQuaternionf rotationDegrees(float f) {
        return new OpenQuaternionf(this, f, true);
    }

    public Vector3f adding(float tx, float ty, float tz) {
        Vector3f ret = copy();
        ret.add(tx, ty, tz);
        return ret;
    }

    public Vector3f adding(Vector3f pos) {
        Vector3f ret = copy();
        ret.add(pos);
        return ret;
    }

    public Vector3f subtracting(float tx, float ty, float tz) {
        Vector3f ret = copy();
        ret.subtract(tx, ty, tz);
        return ret;
    }

    public Vector3f subtracting(Vector3f pos) {
        Vector3f ret = copy();
        ret.subtract(pos);
        return ret;
    }

    public Vector3f scaling(float scale) {
        Vector3f ret = copy();
        ret.scale(scale);
        return ret;
    }

    public Vector3f scaling(float sx, float sy, float sz) {
        Vector3f ret = copy();
        ret.scale(sx, sy, sz);
        return ret;
    }

    public Vector3f transforming(IMatrix3f mat) {
        Vector3f ret = copy();
        ret.transform(mat);
        return ret;
    }

    public Vector3f transforming(IMatrix4f mat) {
        Vector3f ret = copy();
        ret.transform(mat);
        return ret;
    }

    public Vector3f transforming(OpenQuaternionf value) {
        Vector3f ret = copy();
        ret.transform(value);
        return ret;
    }

    public Vector3f normalizing() {
        Vector3f ret = copy();
        ret.normalize();
        return ret;
    }

    public Vector3f crossing(Vector3f pos) {
        Vector3f ret = copy();
        ret.cross(pos);
        return ret;
    }

    public Vector3f clamping(float minValue, float maxValue) {
        Vector3f ret = copy();
        ret.clamp(minValue, maxValue);
        return ret;
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return String.format("(%g %g %g)", x, y, z);
    }
}

