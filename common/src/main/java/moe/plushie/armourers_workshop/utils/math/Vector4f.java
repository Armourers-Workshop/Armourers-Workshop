package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.Position;

import java.util.Objects;

@SuppressWarnings("unused")
public class Vector4f {

    public static final Vector4f ONE = new Vector4f(1, 1, 1, 1);
    public static final Vector4f ZERO = new Vector4f(0, 0, 0, 0);

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

    public Vector4f(Vector4f pos) {
        this(pos.x, pos.y, pos.z, pos.w);
    }

    public Vector4f(Position pos) {
        this((float) pos.x(), (float) pos.y(), (float) pos.z(), 1.0F);
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

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void add(float tx, float ty, float tz, float tw) {
        x += tx;
        y += ty;
        z += tz;
        w += tw;
    }

    public void add(Vector4f pos) {
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

    public void subtract(Vector4f pos) {
        x -= pos.x;
        y -= pos.y;
        z -= pos.z;
        w -= pos.w;
    }

    public void scale(float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        w *= scale;
    }

    public void scale(float sx, float sy, float sz, float sw) {
        x *= sx;
        y *= sy;
        z *= sz;
        w *= sw;
    }

    public void scale(Vector4f pos) {
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
        OpenQuaternionf quaternion = new OpenQuaternionf(q);
        quaternion.mul(new OpenQuaternionf(x, y, z, 0.0F));
        OpenQuaternionf quaternion1 = new OpenQuaternionf(q);
        quaternion1.conjugate();
        quaternion.mul(quaternion1);
        set(quaternion.x(), quaternion.y(), quaternion.z(), w);
    }

    public float dot(Vector4f pos) {
        return x * pos.x + y * pos.y + z * pos.z + w * pos.w;
    }

    public boolean normalize() {
        float f = x * x + y * y + z * z + w * w;
        if ((double) f < 1.0E-5D) {
            return false;
        }
        float f1 = MathUtils.fastInvSqrt(f);
        x *= f1;
        y *= f1;
        z *= f1;
        w *= f1;
        return true;
    }

    public Vector4f scaling(float scale) {
        Vector4f ret = copy();
        ret.scale(scale);
        return ret;
    }

    public Vector4f scaling(float sx, float sy, float sz, float sw) {
        Vector4f ret = copy();
        ret.scale(sx, sy, sz, sw);
        return ret;
    }

    public Vector4f scaling(Vector4f pos) {
        Vector4f ret = copy();
        ret.scale(pos);
        return ret;
    }

    public Vector4f adding(float tx, float ty, float tz, float tw) {
        Vector4f ret = copy();
        ret.add(tx, ty, tz, tw);
        return ret;
    }

    public Vector4f adding(Vector4f pos) {
        Vector4f ret = copy();
        ret.add(pos);
        return ret;
    }

    public Vector4f subtracting(float tx, float ty, float tz, float tw) {
        Vector4f ret = copy();
        ret.subtract(tx, ty, tz, tw);
        return ret;
    }

    public Vector4f subtracting(Vector4f pos) {
        Vector4f ret = copy();
        ret.subtract(pos);
        return ret;
    }

    public Vector4f transforming(IMatrix4f mat) {
        Vector4f ret = copy();
        ret.transform(mat);
        return ret;
    }

    public Vector4f transforming(OpenQuaternionf value) {
        Vector4f ret = copy();
        ret.transform(value);
        return ret;
    }

    public Vector4f normalizing() {
        Vector4f ret = copy();
        ret.normalize();
        return ret;
    }

    public Vector4f copy() {
        return new Vector4f(x, y, z, w);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector4f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0 && Float.compare(w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return String.format("(%g %g %g %g)", x, y, z, w);
    }
}

