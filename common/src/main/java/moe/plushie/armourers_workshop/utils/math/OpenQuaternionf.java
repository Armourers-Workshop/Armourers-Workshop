package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MathUtils;

@SuppressWarnings("unused")
public class OpenQuaternionf implements IQuaternionf {

    public static final OpenQuaternionf ONE = new OpenQuaternionf();

    public float x;
    public float y;
    public float z;
    public float w;

    public OpenQuaternionf() {
        this(0f, 0f, 0f, 1f);
    }

    public OpenQuaternionf(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public OpenQuaternionf(Vector3f vec, float f, boolean bl) {
        if (bl) {
            f *= (float) Math.PI / 180;
        }
        float g = MathUtils.sin(f / 2.0f);
        this.x = vec.getX() * g;
        this.y = vec.getY() * g;
        this.z = vec.getZ() * g;
        this.w = MathUtils.cos(f / 2.0f);
    }

    public OpenQuaternionf(float f, float g, float h, boolean bl) {
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

    public OpenQuaternionf(IQuaternionf other) {
        this.x = other.x();
        this.y = other.y();
        this.z = other.z();
        this.w = other.w();
    }

    public static OpenQuaternionf fromXYZ(float angleX, float angleY, float angleZ) {
        float sx = MathUtils.sin(angleX * 0.5f);
        float cx = MathUtils.cosFromSin(sx, angleX * 0.5f);
        float sy = MathUtils.sin(angleY * 0.5f);
        float cy = MathUtils.cosFromSin(sy, angleY * 0.5f);
        float sz = MathUtils.sin(angleZ * 0.5f);
        float cz = MathUtils.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternionf(sx * cycz + cx * sysz, cx * sycz - sx * cysz, cx * cysz + sx * sycz, cx * cycz - sx * sysz);
    }

    public static OpenQuaternionf fromZYX(float angleZ, float angleY, float angleX) {
        float sx = MathUtils.sin(angleX * 0.5f);
        float cx = MathUtils.cosFromSin(sx, angleX * 0.5f);
        float sy = MathUtils.sin(angleY * 0.5f);
        float cy = MathUtils.cosFromSin(sy, angleY * 0.5f);
        float sz = MathUtils.sin(angleZ * 0.5f);
        float cz = MathUtils.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternionf(sx * cycz - cx * sysz, cx * sycz + sx * cysz, cx * cysz - sx * sycz, cx * cycz + sx * sysz);
    }

    public static OpenQuaternionf fromYXZ(float angleY, float angleX, float angleZ) {
        float sx = MathUtils.sin(angleX * 0.5f);
        float cx = MathUtils.cosFromSin(sx, angleX * 0.5f);
        float sy = MathUtils.sin(angleY * 0.5f);
        float cy = MathUtils.cosFromSin(sy, angleY * 0.5f);
        float sz = MathUtils.sin(angleZ * 0.5f);
        float cz = MathUtils.cosFromSin(sz, angleZ * 0.5f);
        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        return new OpenQuaternionf(x * cz + y * sz, y * cz - x * sz, w * sz - z * cz, w * cz + z * sz);
    }

//    public Quaternionf setAngleAxis(float angle, float x, float y, float z) {
//        float s = Math.sin(angle * 0.5f);
//        this.x = x * s;
//        this.y = y * s;
//        this.z = z * s;
//        this.w = Math.cosFromSin(s, angle * 0.5f);
//        return this;


    //    public static OpenQuaternionf fromYXZ(float f, float g, float h) {
//        OpenQuaternionf quaternion = ONE.copy();
//        quaternion.mul(new OpenQuaternionf(0.0f, (float) Math.sin(f / 2.0f), 0.0f, (float) Math.cos(f / 2.0f)));
//        quaternion.mul(new OpenQuaternionf((float) Math.sin(g / 2.0f), 0.0f, 0.0f, (float) Math.cos(g / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
//        return quaternion;
//    }
//
//    public static OpenQuaternionf fromXYZDegrees(Vector3f vector3f) {
//        return fromXYZ((float) Math.toRadians(vector3f.x()), (float) Math.toRadians(vector3f.y()), (float) Math.toRadians(vector3f.z()));
//    }
//
//    public static OpenQuaternionf fromXYZ(Vector3f vector3f) {
//        return fromXYZ(vector3f.getX(), vector3f.getY(), vector3f.getZ());
//    }
//
//    public static OpenQuaternionf fromUnitVectors(Vector3f from, Vector3f to) {
//        // assumes direction vectors vFrom and vTo are normalized
//        float EPS = 0.000001f;
//        float x, y, z, r = from.dot(to) + 1;
//        if (r < EPS) {
//            r = 0f;
//            if (Math.abs(from.getX()) > Math.abs(from.getZ())) {
//                x = -from.getY();
//                y = from.getX();
//                z = 0f;
//            } else {
//                x = 0f;
//                y = -from.getZ();
//                z = from.getY();
//            }
//        } else {
//            // crossVectors( vFrom, vTo ); // inlined to avoid cyclic dependency on Vector3
//            x = from.getY() * to.getZ() - from.getZ() * to.getY();
//            y = from.getZ() * to.getX() - from.getX() * to.getZ();
//            z = from.getX() * to.getY() - from.getY() * to.getX();
//        }
//        return new OpenQuaternionf(x, y, z, r).normalize();
//    }
//
//    public static OpenQuaternionf fromXYZ(float f, float g, float h) {
//        OpenQuaternionf quaternion = ONE.copy();
//        quaternion.mul(new OpenQuaternionf((float) Math.sin(f / 2.0f), 0.0f, 0.0f, (float) Math.cos(f / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, (float) Math.sin(g / 2.0f), 0.0f, (float) Math.cos(g / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
//        return quaternion;
//    }
//
    public Vector3f toXYZ() {
        float f = w * w;
        float g = x * x;
        float h = y * y;
        float i = z * z;
        float j = f + g + h + i;
        float k = 2.0f * w * x - 2.0f * y * z;
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(2.0f * (float) Math.atan2(x, w), l, 0.0f);
        }
        return new Vector3f((float) Math.atan2(2.0f * y * z + 2.0f * x * w, f - g - h + i), l, (float) Math.atan2(2.0f * x * y + 2.0f * w * z, f + g - h - i));
    }

    public Vector3f toYXZ() {
        float f = w * w;
        float g = x * x;
        float h = y * y;
        float i = z * z;
        float j = f + g + h + i;
        float k = 2.0f * w * x - 2.0f * y * z;
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(l, 2.0f * (float) Math.atan2(y, w), 0.0f);
        }
        return new Vector3f(l, (float) Math.atan2(2.0f * x * z + 2.0f * y * w, f - g - h + i), (float) Math.atan2(2.0f * x * y + 2.0f * w * z, f - g + h - i));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        OpenQuaternionf quaternion = (OpenQuaternionf) object;
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
        stringBuilder.append("Quaternionf[").append(w).append(" + ");
        stringBuilder.append(x).append("i + ");
        stringBuilder.append(y).append("j + ");
        stringBuilder.append(z).append("k]");
        return stringBuilder.toString();
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    @Override
    public float z() {
        return this.z;
    }

    @Override
    public float w() {
        return this.w;
    }

    public void mul(OpenQuaternionf other) {
        float f = x;
        float g = y;
        float h = z;
        float i = w;
        float j = other.x;
        float k = other.y;
        float l = other.z;
        float m = other.w;
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

    public float dot(OpenQuaternionf other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public OpenQuaternionf conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public OpenQuaternionf inverse() {
        return this.conjugate();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public void set(OpenQuaternionf other) {
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

    public OpenQuaternionf normalize() {
        float f = x * x + y * y + z * z + w * w;
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

    public void slerp(OpenQuaternionf quaternion, float f) {
        throw new UnsupportedOperationException();
    }

    public OpenQuaternionf copy() {
        return new OpenQuaternionf(this);
    }
}
