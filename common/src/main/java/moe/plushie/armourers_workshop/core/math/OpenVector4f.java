package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.nio.FloatBuffer;

@SuppressWarnings("unused")
public class OpenVector4f {

    public static final OpenVector4f ONE = new OpenVector4f(1, 1, 1, 1);
    public static final OpenVector4f ZERO = new OpenVector4f(0, 0, 0, 0);

    public float x;
    public float y;
    public float z;
    public float w;

    public OpenVector4f() {
    }

    public OpenVector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public OpenVector4f(IVector3f pos) {
        this(pos.x(), pos.y(), pos.z(), 1.0f);
    }

    public OpenVector4f(OpenVector4f pos) {
        this(pos.x, pos.y, pos.z, pos.w);
    }

    public OpenVector4f(FloatBuffer buffer) {
        this(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3));
    }

    public void set(OpenVector4f pos) {
        set(pos.x, pos.y, pos.z, pos.w);
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public float w() {
        return w;
    }

    public void add(float tx, float ty, float tz, float tw) {
        x += tx;
        y += ty;
        z += tz;
        w += tw;
    }

    public void add(OpenVector4f pos) {
        x += pos.x;
        y += pos.y;
        z += pos.z;
        w += pos.w;
    }

    public void subtract(float tx, float ty, float tz, float tw) {
        x -= tx;
        y -= ty;
        z -= tz;
        w += tw;
    }

    public void subtract(OpenVector4f pos) {
        x -= pos.x;
        y -= pos.y;
        z -= pos.z;
        w -= pos.w;
    }

    public void scale(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public void scale(float sx, float sy, float sz, float sw) {
        x *= sx;
        y *= sy;
        z *= sz;
        w *= sw;
    }

    public void scale(OpenVector4f pos) {
        x *= pos.x;
        y *= pos.y;
        z *= pos.z;
        w *= pos.w;
    }

    public void transform(IMatrix4f matrix) {
        float[] floats = {x, y, z, w};
        matrix.multiply(floats);
        set(floats[0], floats[1], floats[2], floats[3]);
    }

    public void transform(OpenQuaternionf q) {
        var quaternion = new OpenQuaternionf(q);
        quaternion.multiply(new OpenQuaternionf(x, y, z, 0.0F));
        var quaternion1 = new OpenQuaternionf(q);
        quaternion1.conjugate();
        quaternion.multiply(quaternion1);
        set(quaternion.x(), quaternion.y(), quaternion.z(), w);
    }

    public float dot(OpenVector4f pos) {
        return x * pos.x + y * pos.y + z * pos.z + w * pos.w;
    }


    public float length() {
        return OpenMath.sqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, OpenMath.fma(z, z, w * w))));
    }

    public void normalize() {
        var scalar = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, OpenMath.fma(z, z, w * w))));
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public OpenVector4f scaling(float scalar) {
        var ret = copy();
        ret.scale(scalar);
        return ret;
    }

    public OpenVector4f scaling(float sx, float sy, float sz, float sw) {
        var ret = copy();
        ret.scale(sx, sy, sz, sw);
        return ret;
    }

    public OpenVector4f scaling(OpenVector4f pos) {
        var ret = copy();
        ret.scale(pos);
        return ret;
    }

    public OpenVector4f adding(float tx, float ty, float tz, float tw) {
        var ret = copy();
        ret.add(tx, ty, tz, tw);
        return ret;
    }

    public OpenVector4f adding(OpenVector4f pos) {
        var ret = copy();
        ret.add(pos);
        return ret;
    }

    public OpenVector4f subtracting(float tx, float ty, float tz, float tw) {
        var ret = copy();
        ret.subtract(tx, ty, tz, tw);
        return ret;
    }

    public OpenVector4f subtracting(OpenVector4f pos) {
        var ret = copy();
        ret.subtract(pos);
        return ret;
    }

    public OpenVector4f transforming(IMatrix4f mat) {
        var ret = copy();
        ret.transform(mat);
        return ret;
    }

    public OpenVector4f transforming(OpenQuaternionf value) {
        var ret = copy();
        ret.transform(value);
        return ret;
    }

    public OpenVector4f normalizing() {
        var ret = copy();
        ret.normalize();
        return ret;
    }

    public OpenVector4f copy() {
        return new OpenVector4f(x, y, z, w);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector4f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0 && Float.compare(w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f %f %f)", x, y, z, w);
    }
}

