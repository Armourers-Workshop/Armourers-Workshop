package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MathUtils;

public class Quaternionf implements IQuaternionf {

    public static final Quaternionf ONE = new Quaternionf();

    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternionf() {
        this(0f, 0f, 0f, 1f);
    }

    public Quaternionf(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternionf(Vector3f vec, float f, boolean bl) {
        if (bl) {
            f *= (float) Math.PI / 180;
        }
        float g = sin(f / 2.0f);
        this.x = vec.getX() * g;
        this.y = vec.getY() * g;
        this.z = vec.getZ() * g;
        this.w = cos(f / 2.0f);
    }

    public Quaternionf(float f, float g, float h, boolean bl) {
        if (bl) {
            f *= (float) Math.PI / 180;
            g *= (float) Math.PI / 180;
            h *= (float) Math.PI / 180;
        }
        float i = sin(0.5f * f);
        float j = cos(0.5f * f);
        float k = sin(0.5f * g);
        float l = cos(0.5f * g);
        float m = sin(0.5f * h);
        float n = cos(0.5f * h);
        this.x = i * l * n + j * k * m;
        this.y = j * k * n - i * l * m;
        this.z = i * k * n + j * l * m;
        this.w = j * l * n - i * k * m;
    }

    public Quaternionf(IQuaternionf other) {
        this.x = other.x();
        this.y = other.y();
        this.z = other.z();
        this.w = other.w();
    }

    public static Quaternionf fromYXZ(float f, float g, float h) {
        Quaternionf quaternion = ONE.copy();
        quaternion.mul(new Quaternionf(0.0f, (float) Math.sin(f / 2.0f), 0.0f, (float) Math.cos(f / 2.0f)));
        quaternion.mul(new Quaternionf((float) Math.sin(g / 2.0f), 0.0f, 0.0f, (float) Math.cos(g / 2.0f)));
        quaternion.mul(new Quaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
        return quaternion;
    }

    public static Quaternionf fromXYZDegrees(Vector3f vector3f) {
        return fromXYZ((float) Math.toRadians(vector3f.x()), (float) Math.toRadians(vector3f.y()), (float) Math.toRadians(vector3f.z()));
    }

    public static Quaternionf fromXYZ(Vector3f vector3f) {
        return fromXYZ(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public static Quaternionf fromUnitVectors(Vector3f from, Vector3f to) {
        // assumes direction vectors vFrom and vTo are normalized
        float EPS = 0.000001f;
        float x, y, z, r = from.dot(to) + 1;
        if (r < EPS) {
            r = 0f;
            if (Math.abs(from.getX()) > Math.abs(from.getZ())) {
                x = -from.getY();
                y = from.getX();
                z = 0f;
            } else {
                x = 0f;
                y = -from.getZ();
                z = from.getY();
            }
        } else {
            // crossVectors( vFrom, vTo ); // inlined to avoid cyclic dependency on Vector3
            x = from.getY() * to.getZ() - from.getZ() * to.getY();
            y = from.getZ() * to.getX() - from.getX() * to.getZ();
            z = from.getX() * to.getY() - from.getY() * to.getX();
        }
        return new Quaternionf(x, y, z, r).normalize();
    }

    public static Quaternionf fromXYZ(float f, float g, float h) {
        Quaternionf quaternion = ONE.copy();
        quaternion.mul(new Quaternionf((float) Math.sin(f / 2.0f), 0.0f, 0.0f, (float) Math.cos(f / 2.0f)));
        quaternion.mul(new Quaternionf(0.0f, (float) Math.sin(g / 2.0f), 0.0f, (float) Math.cos(g / 2.0f)));
        quaternion.mul(new Quaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
        return quaternion;
    }

    public Vector3f toXYZ() {
        float f = this.w() * this.w();
        float g = this.x() * this.x();
        float h = this.y() * this.y();
        float i = this.z() * this.z();
        float j = f + g + h + i;
        float k = 2.0f * this.w() * this.x() - 2.0f * this.y() * this.z();
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(2.0f * (float) Math.atan2(this.x(), this.w()), l, 0.0f);
        }
        return new Vector3f((float) Math.atan2(2.0f * this.y() * this.z() + 2.0f * this.x() * this.w(), f - g - h + i), l, (float) Math.atan2(2.0f * this.x() * this.y() + 2.0f * this.w() * this.z(), f + g - h - i));
    }

    public Vector3f toXYZDegrees() {
        Vector3f vector3f = this.toXYZ();
        return new Vector3f((float) Math.toDegrees(vector3f.x()), (float) Math.toDegrees(vector3f.y()), (float) Math.toDegrees(vector3f.z()));
    }

    public Vector3f toYXZ() {
        float f = this.w() * this.w();
        float g = this.x() * this.x();
        float h = this.y() * this.y();
        float i = this.z() * this.z();
        float j = f + g + h + i;
        float k = 2.0f * this.w() * this.x() - 2.0f * this.y() * this.z();
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(l, 2.0f * (float) Math.atan2(this.y(), this.w()), 0.0f);
        }
        return new Vector3f(l, (float) Math.atan2(2.0f * this.x() * this.z() + 2.0f * this.y() * this.w(), f - g - h + i), (float) Math.atan2(2.0f * this.x() * this.y() + 2.0f * this.w() * this.z(), f - g + h - i));
    }

    public Vector3f toYXZDegrees() {
        Vector3f vector3f = this.toYXZ();
        return new Vector3f((float) Math.toDegrees(vector3f.x()), (float) Math.toDegrees(vector3f.y()), (float) Math.toDegrees(vector3f.z()));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Quaternionf quaternion = (Quaternionf) object;
        if (Float.compare(quaternion.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(quaternion.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(quaternion.z, this.z) != 0) {
            return false;
        }
        return Float.compare(quaternion.w, this.w) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternionf[").append(this.w()).append(" + ");
        stringBuilder.append(this.x()).append("i + ");
        stringBuilder.append(this.y()).append("j + ");
        stringBuilder.append(this.z()).append("k]");
        return stringBuilder.toString();
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

    public void mul(Quaternionf quaternion) {
        float f = this.x();
        float g = this.y();
        float h = this.z();
        float i = this.w();
        float j = quaternion.x();
        float k = quaternion.y();
        float l = quaternion.z();
        float m = quaternion.w();
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public float dot(Quaternionf other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public Quaternionf conj() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Quaternionf inverse() {
        return this.conj();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public void set(Quaternionf other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    private static float cos(float f) {
        return (float) Math.cos(f);
    }

    private static float sin(float f) {
        return (float) Math.sin(f);
    }

    public Quaternionf normalize() {
        float f = this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.w() * this.w();
        if (f > 1.0E-6f) {
            float g = MathUtils.fastInvSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
        return this;
    }

    public void slerp(Quaternionf quaternion, float f) {
        throw new UnsupportedOperationException();
    }

    public Quaternionf copy() {
        return new Quaternionf(this);
    }
}
