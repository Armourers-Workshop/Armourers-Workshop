package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;

@SuppressWarnings("unused")
public class OpenVector3f implements Comparable<OpenVector3f>, IVector3f {

    public static final int BYTES = Float.BYTES * 3;

    public static OpenVector3f ZERO = new OpenVector3f(0.0f, 0.0f, 0.0f);
    public static OpenVector3f ONE = new OpenVector3f(1.0f, 1.0f, 1.0f);

    public static OpenVector3f XN = new OpenVector3f(-1.0f, 0.0f, 0.0f);
    public static OpenVector3f XP = new OpenVector3f(1.0f, 0.0f, 0.0f);
    public static OpenVector3f YN = new OpenVector3f(0.0f, -1.0f, 0.0f);
    public static OpenVector3f YP = new OpenVector3f(0.0f, 1.0f, 0.0f);
    public static OpenVector3f ZN = new OpenVector3f(0.0f, 0.0f, -1.0f);
    public static OpenVector3f ZP = new OpenVector3f(0.0f, 0.0f, 1.0f);

    public static final IDataCodec<OpenVector3f> CODEC = IDataCodec.FLOAT.listOf().xmap(OpenVector3f::new, OpenVector3f::toList);

    public float x;
    public float y;
    public float z;

    public OpenVector3f() {
    }

    public OpenVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public OpenVector3f(double x, double y, double z) {
        this((float) x, (float) y, (float) z);
    }

    public OpenVector3f(IVector3f pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3f(IVector3i pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3f(List<Float> values) {
        this(values.get(0), values.get(1), values.get(2));
    }

    public OpenVector3f(float[] values) {
        set(values);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(OpenVector3f pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
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

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z() {
        return z;
    }


    public void add(float tx, float ty, float tz) {
        x += tx;
        y += ty;
        z += tz;
    }

    public void add(OpenVector3f pos) {
        x += pos.x;
        y += pos.y;
        z += pos.z;
    }

    public void subtract(float tx, float ty, float tz) {
        x -= tx;
        y -= ty;
        z -= tz;
    }

    public void subtract(OpenVector3f pos) {
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

    public void scale(OpenVector3f pos) {
        x *= pos.x;
        y *= pos.y;
        z *= pos.z;
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
        var quaternion = new OpenQuaternionf(value);
        quaternion.multiply(new OpenQuaternionf(x, y, z, 0.0F));
        var quaternion1 = new OpenQuaternionf(value);
        quaternion1.conjugate();
        quaternion.multiply(quaternion1);
        set(quaternion.x(), quaternion.y(), quaternion.z());
    }

    public void normalize() {
        var scalar = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public void cross(OpenVector3f pos) {
        float ax = x;
        float ay = y;
        float az = z;
        float bx = pos.x();
        float by = pos.y();
        float bz = pos.z();
        this.x = ay * bz - az * by;
        this.y = az * bx - ax * bz;
        this.z = ax * by - ay * bx;
    }

    public void clamp(float minValue, float maxValue) {
        x = OpenMath.clamp(x, minValue, maxValue);
        y = OpenMath.clamp(y, minValue, maxValue);
        z = OpenMath.clamp(z, minValue, maxValue);
    }

    public void lerp(OpenVector3f pos, float f) {
        float f1 = 1.0F - f;
        this.x = x * f1 + pos.x * f;
        this.y = y * f1 + pos.y * f;
        this.z = z * f1 + pos.z * f;
    }

    public float dot(OpenVector3f pos) {
        return x * pos.x + y * pos.y + z * pos.z;
    }

    public float length() {
        return OpenMath.sqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
    }

    /**
     * Computes distance of Vector3 vector to pos.
     */
    public float distanceTo(OpenVector3f pos) {
        return OpenMath.sqrt(distanceToSquared(pos));
    }

    /**
     * Computes squared distance of Vector3 vector to v.
     */
    public float distanceToSquared(OpenVector3f pos) {
        return distanceToSquared(pos.x, pos.y, pos.z);
    }

    public float distanceToSquared(float tx, float ty, float tz) {
        float dx = x - tx;
        float dy = y - ty;
        float dz = z - tz;
        return OpenMath.fma(dx, dx, OpenMath.fma(dy, dy, dz * dz));
    }

    public OpenQuaternionf rotation(float f) {
        return new OpenQuaternionf(this, f, false);
    }

    public OpenQuaternionf rotationDegrees(float f) {
        return new OpenQuaternionf(this, f, true);
    }

    public OpenVector3f adding(float tx, float ty, float tz) {
        var ret = copy();
        ret.add(tx, ty, tz);
        return ret;
    }

    public OpenVector3f adding(OpenVector3f pos) {
        var ret = copy();
        ret.add(pos);
        return ret;
    }

    public OpenVector3f subtracting(float tx, float ty, float tz) {
        var ret = copy();
        ret.subtract(tx, ty, tz);
        return ret;
    }

    public OpenVector3f subtracting(OpenVector3f pos) {
        var ret = copy();
        ret.subtract(pos);
        return ret;
    }

    public OpenVector3f scaling(float scale) {
        var ret = copy();
        ret.scale(scale);
        return ret;
    }

    public OpenVector3f scaling(float sx, float sy, float sz) {
        var ret = copy();
        ret.scale(sx, sy, sz);
        return ret;
    }

    public OpenVector3f scaling(OpenVector3f pos) {
        var ret = copy();
        ret.scale(pos);
        return ret;
    }

    public OpenVector3f transforming(IMatrix3f mat) {
        var ret = copy();
        ret.transform(mat);
        return ret;
    }

    public OpenVector3f transforming(IMatrix4f mat) {
        var ret = copy();
        ret.transform(mat);
        return ret;
    }

    public OpenVector3f transforming(OpenQuaternionf value) {
        var ret = copy();
        ret.transform(value);
        return ret;
    }

    public OpenVector3f normalizing() {
        var ret = copy();
        ret.normalize();
        return ret;
    }

    public OpenVector3f crossing(OpenVector3f pos) {
        var ret = copy();
        ret.cross(pos);
        return ret;
    }

    public OpenVector3f clamping(float minValue, float maxValue) {
        var ret = copy();
        ret.clamp(minValue, maxValue);
        return ret;
    }

    public OpenVector3f copy() {
        return new OpenVector3f(x, y, z);
    }

    public List<Float> toList() {
        return Collections.newList(x, y, z);
    }

    @Override
    public int compareTo(OpenVector3f v) {
        int dy = Float.compare(y(), v.y());
        if (dy != 0) {
            return dy;
        }
        int dz = Float.compare(z(), v.z());
        if (dz != 0) {
            return dz;
        }
        return Float.compare(x(), v.x());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector3f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f %f)", x, y, z);
    }
}

