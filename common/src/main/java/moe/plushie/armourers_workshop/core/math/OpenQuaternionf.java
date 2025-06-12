package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenQuaternionf implements IQuaternionf {

    public static final OpenQuaternionf ONE = new OpenQuaternionf();

    private static final float DEGREES_TO_RADIANS = OpenMath.PI_f / 180;

    private float x;
    private float y;
    private float z;
    private float w;

    public OpenQuaternionf() {
        this(0f, 0f, 0f, 1f);
    }

    public OpenQuaternionf(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public OpenQuaternionf(OpenVector3f vec, float f, boolean bl) {
        if (bl) {
            f *= DEGREES_TO_RADIANS;
        }
        var g = OpenMath.sin(f / 2.0f);
        this.x = vec.x() * g;
        this.y = vec.y() * g;
        this.z = vec.z() * g;
        this.w = OpenMath.cos(f / 2.0f);
    }

    public OpenQuaternionf(float x, float y, float z, boolean bl) {
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        float i = OpenMath.sin(0.5f * x);
        float j = OpenMath.cos(0.5f * x);
        float k = OpenMath.sin(0.5f * y);
        float l = OpenMath.cos(0.5f * y);
        float m = OpenMath.sin(0.5f * z);
        float n = OpenMath.cos(0.5f * z);
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

    public static OpenQuaternionf fromEulerAnglesXYZ(float angleX, float angleY, float angleZ) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternionf(sx * cycz + cx * sysz, cx * sycz - sx * cysz, cx * cysz + sx * sycz, cx * cycz - sx * sysz);
    }

    public static OpenQuaternionf fromEulerAnglesZYX(float angleZ, float angleY, float angleX) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternionf(sx * cycz - cx * sysz, cx * sycz + sx * cysz, cx * cysz - sx * sycz, cx * cycz + sx * sysz);
    }

    public static OpenQuaternionf fromEulerAnglesYXZ(float angleY, float angleX, float angleZ) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        return new OpenQuaternionf(x * cz + y * sz, y * cz - x * sz, w * sz - z * cz, w * cz + z * sz);
    }

    public static OpenQuaternionf fromEulerAnglesXYZ(float x, float y, float z, boolean bl) {
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromEulerAnglesXYZ(x, y, z);
    }

    public static OpenQuaternionf fromEulerAnglesZYX(float z, float y, float x, boolean bl) {
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromEulerAnglesZYX(z, y, x);
    }

    public static OpenQuaternionf fromEulerAnglesYXZ(float y, float x, float z, boolean bl) {
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromEulerAnglesYXZ(y, x, z);
    }

    public static OpenQuaternionf fromEulerAnglesXYZ(IVector3f value, boolean bl) {
        return fromEulerAnglesXYZ(value.x(), value.y(), value.z(), bl);
    }

    public static OpenQuaternionf fromEulerAnglesZYX(IVector3f value, boolean bl) {
        return fromEulerAnglesZYX(value.z(), value.y(), value.x(), bl);
    }

    public static OpenQuaternionf fromEulerAnglesYXZ(IVector3f value, boolean bl) {
        return fromEulerAnglesYXZ(value.y(), value.x(), value.z(), bl);
    }


    public static OpenQuaternionf fromNormalizedMatrix(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        float x, y, z, w, t;
        float tr = m00 + m11 + m22;
        if (tr >= 0.0f) {
            t = OpenMath.sqrt(tr + 1.0f);
            w = t * 0.5f;
            t = 0.5f / t;
            x = (m12 - m21) * t;
            y = (m20 - m02) * t;
            z = (m01 - m10) * t;
        } else if (m00 >= m11 && m00 >= m22) {
            t = OpenMath.sqrt(m00 - (m11 + m22) + 1.0f);
            x = t * 0.5f;
            t = 0.5f / t;
            y = (m10 + m01) * t;
            z = (m02 + m20) * t;
            w = (m12 - m21) * t;
        } else if (m11 > m22) {
            t = OpenMath.sqrt(m11 - (m22 + m00) + 1.0f);
            y = t * 0.5f;
            t = 0.5f / t;
            z = (m21 + m12) * t;
            x = (m10 + m01) * t;
            w = (m20 - m02) * t;
        } else {
            t = OpenMath.sqrt(m22 - (m00 + m11) + 1.0f);
            z = t * 0.5f;
            t = 0.5f / t;
            x = (m02 + m20) * t;
            y = (m21 + m12) * t;
            w = (m01 - m10) * t;
        }
        return new OpenQuaternionf(x, y, z, w);
    }

    public static OpenQuaternionf fromUnnormalizedMatrix(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        float lenX = OpenMath.invsqrt(m00 * m00 + m01 * m01 + m02 * m02);
        float lenY = OpenMath.invsqrt(m10 * m10 + m11 * m11 + m12 * m12);
        float lenZ = OpenMath.invsqrt(m20 * m20 + m21 * m21 + m22 * m22);
        m00 *= lenX;
        m01 *= lenX;
        m02 *= lenX;
        m10 *= lenY;
        m11 *= lenY;
        m12 *= lenY;
        m20 *= lenZ;
        m21 *= lenZ;
        m22 *= lenZ;
        return fromNormalizedMatrix(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public static OpenQuaternionf fromNormalizedMatrix(IMatrix3f matrix) {
        var mat = OpenMatrix3f.of(matrix);
        return fromNormalizedMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
    }

    public static OpenQuaternionf fromNormalizedMatrix(IMatrix4f matrix) {
        var mat = OpenMatrix4f.of(matrix);
        return fromNormalizedMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
    }

    public static OpenQuaternionf fromUnnormalizedMatrix(IMatrix3f matrix) {
        var mat = OpenMatrix3f.of(matrix);
        return fromUnnormalizedMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
    }

    public static OpenQuaternionf fromUnnormalizedMatrix(IMatrix4f matrix) {
        var mat = OpenMatrix4f.of(matrix);
        return fromUnnormalizedMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
    }

    public OpenVector3f eulerAnglesXYZ() {
        float tx = OpenMath.atan2(x * w - y * z, 0.5f - x * x - y * y);
        float ty = OpenMath.safeAsin(2.0f * (x * z + y * w));
        float tz = OpenMath.atan2(z * w - x * y, 0.5f - y * y - z * z);
        return new OpenVector3f(tx, ty, tz);
    }

    public OpenVector3f eulerAnglesZYX() {
        float tx = OpenMath.atan2(y * z + w * x, 0.5f - x * x + y * y);
        float ty = OpenMath.safeAsin(-2.0f * (x * z - w * y));
        float tz = OpenMath.atan2(x * y + w * z, 0.5f - y * y - z * z);
        return new OpenVector3f(tx, ty, tz);
    }

    public OpenVector3f eulerAnglesZXY() {
        float tx = OpenMath.safeAsin(2.0f * (w * x + y * z));
        float ty = OpenMath.atan2(w * y - x * z, 0.5f - y * y - x * x);
        float tz = OpenMath.atan2(w * z - x * y, 0.5f - z * z - x * x);
        return new OpenVector3f(tx, ty, tz);
    }

    public OpenVector3f eulerAnglesYXZ() {
        float tx = OpenMath.safeAsin(-2.0f * (y * z - w * x));
        float ty = OpenMath.atan2(x * z + y * w, 0.5f - y * y - x * x);
        float tz = OpenMath.atan2(y * x + w * z, 0.5f - x * x - z * z);
        return new OpenVector3f(tx, ty, tz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenQuaternionf that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0 && Float.compare(w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    public String toString() {
        return OpenMath.format("(%f %f %f %f)", x, y, z, w);
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

    public void setW(float w) {
        this.w = w;
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

    public OpenQuaternionf normalize() {
        var invNorm = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, OpenMath.fma(z, z, w * w))));
        x *= invNorm;
        y *= invNorm;
        z *= invNorm;
        w *= invNorm;
        return this;
    }

    public OpenQuaternionf copy() {
        return new OpenQuaternionf(this);
    }
}
