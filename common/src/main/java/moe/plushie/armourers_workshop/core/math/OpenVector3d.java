package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IVector3d;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.core.BlockPos;

@SuppressWarnings("unused")
public class OpenVector3d implements IVector3d {

    public static final OpenVector3d ZERO = new OpenVector3d(0.0D, 0.0D, 0.0D);
    public static final OpenVector3d ONE = new OpenVector3d(1.0D, 1.0D, 1.0D);

    public double x;
    public double y;
    public double z;

    public OpenVector3d() {
    }

    public OpenVector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public OpenVector3d(IVector3i pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3d(IVector3f pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3d(IVector3d pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public static OpenVector3d fromRGB24(int rgb) {
        var d0 = (double) (rgb >> 16 & 255) / 255.0D;
        var d1 = (double) (rgb >> 8 & 255) / 255.0D;
        var d2 = (double) (rgb & 255) / 255.0D;
        return new OpenVector3d(d0, d1, d2);
    }

    public static OpenVector3d atCenterOf(BlockPos pos) {
        return new OpenVector3d((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    public static OpenVector3d atLowerCornerOf(BlockPos pos) {
        return new OpenVector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static OpenVector3d atBottomCenterOf(BlockPos pos) {
        return new OpenVector3d((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D);
    }

    public static OpenVector3d upFromBottomCenterOf(BlockPos pos, double offset) {
        return new OpenVector3d((double) pos.getX() + 0.5D, (double) pos.getY() + offset, (double) pos.getZ() + 0.5D);
    }

    public static OpenVector3d directionFromRotation(float a, float b) {
        var f = Math.cos(-b * (Math.PI / 180.0) - Math.PI);
        var f1 = Math.sin(-b * (Math.PI / 180.0) - Math.PI);
        var f2 = -Math.cos(-a * (Math.PI / 180.0));
        var f3 = Math.sin(-a * (Math.PI / 180.0));
        return new OpenVector3d(f1 * f2, f3, f * f2);
    }

    public OpenVector3d vectorTo(OpenVector3d pos) {
        return new OpenVector3d(pos.x - this.x, pos.y - this.y, pos.z - this.z);
    }

    public double distanceTo(IVector3f pos) {
        return distanceTo(pos.x(), pos.y(), pos.z());
    }

    public double distanceTo(IVector3d pos) {
        return distanceTo(pos.x(), pos.y(), pos.z());
    }

    public double distanceToSqr(IVector3f pos) {
        return distanceToSqr(pos.x(), pos.y(), pos.z());
    }

    public double distanceToSqr(IVector3d pos) {
        return distanceToSqr(pos.x(), pos.y(), pos.z());
    }

    public double distanceTo(double tx, double ty, double tz) {
        return Math.sqrt(distanceToSqr(tx, ty, tz));
    }

    public double distanceToSqr(double tx, double ty, double tz) {
        var d0 = tx - x;
        var d1 = ty - y;
        var d2 = tz - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public OpenVector3d multiply(OpenVector3d pos) {
        return multiply(pos.x, pos.y, pos.z);
    }

    public OpenVector3d multiply(double dx, double dy, double dz) {
        return new OpenVector3d(x * dx, y * dy, z * dz);
    }

    public void add(double tx, double ty, double tz) {
        this.x += tx;
        this.y += ty;
        this.z += tz;
    }

    public void add(OpenVector3d pos) {
        this.x += pos.x;
        this.y += pos.y;
        this.z += pos.z;
    }

    public void subtract(double tx, double ty, double tz) {
        this.x -= tx;
        this.y -= ty;
        this.z -= tz;
    }

    public void subtract(OpenVector3d pos) {
        this.x -= pos.x;
        this.y -= pos.y;
        this.z -= pos.z;
    }

    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public void scale(double sx, double sy, double sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }

    public void scale(OpenVector3d pos) {
        this.x *= pos.x;
        this.y *= pos.y;
        this.z *= pos.z;
    }

    public void normalize() {
        var scalar = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public void cross(OpenVector3d pos) {
        var ax = x;
        var ay = y;
        var az = z;
        var bx = pos.x();
        var by = pos.y();
        var bz = pos.z();
        this.x = ay * bz - az * by;
        this.y = az * bx - ax * bz;
        this.z = ax * by - ay * bx;
    }

    public void clamp(double minValue, double maxValue) {
        this.x = OpenMath.clamp(x, minValue, maxValue);
        this.y = OpenMath.clamp(y, minValue, maxValue);
        this.z = OpenMath.clamp(z, minValue, maxValue);
    }

    public double length() {
        return OpenMath.sqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
    }

    public double dot(OpenVector3d vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector3d that)) return false;
        return Double.compare(x, that.x) == 0 && Double.compare(y, that.y) == 0 && Double.compare(z, that.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%lf %lf %lf)", x, y, z);
    }


    public OpenVector3d reverse() {
        return scaling(-1.0);
    }

    public OpenVector3d adding(double tx, double ty, double tz) {
        var ret = copy();
        ret.add(tx, ty, tz);
        return ret;
    }

    public OpenVector3d adding(OpenVector3d pos) {
        var ret = copy();
        ret.add(pos);
        return ret;
    }

    public OpenVector3d subtracting(double tx, double ty, double tz) {
        var ret = copy();
        ret.subtract(tx, ty, tz);
        return ret;
    }

    public OpenVector3d subtracting(OpenVector3d pos) {
        var ret = copy();
        ret.subtract(pos);
        return ret;
    }

    public OpenVector3d scaling(double scale) {
        var ret = copy();
        ret.scale(scale);
        return ret;
    }

    public OpenVector3d scaling(double sx, double sy, double sz) {
        var ret = copy();
        ret.scale(sx, sy, sz);
        return ret;
    }

    public OpenVector3d scaling(OpenVector3d pos) {
        var ret = copy();
        ret.scale(pos);
        return ret;
    }

//    public OpenVector3d transforming(IMatrix3f mat) {
//        var ret = copy();
//        ret.transform(mat);
//        return ret;
//    }
//
//    public OpenVector3d transforming(IMatrix4f mat) {
//        var ret = copy();
//        ret.transform(mat);
//        return ret;
//    }
//
//    public OpenVector3d transforming(OpenQuaternionf value) {
//        var ret = copy();
//        ret.transform(value);
//        return ret;
//    }

    public OpenVector3d normalizing() {
        var ret = copy();
        ret.normalize();
        return ret;
    }

    public OpenVector3d crossing(OpenVector3d pos) {
        var ret = copy();
        ret.cross(pos);
        return ret;
    }

    public OpenVector3d clamping(double minValue, double maxValue) {
        var ret = copy();
        ret.clamp(minValue, maxValue);
        return ret;
    }

    public OpenVector3d xRot(double value) {
        var f = Math.cos(value);
        var f1 = Math.sin(value);
        var d0 = x;
        var d1 = y * f + z * f1;
        var d2 = z * f - y * f1;
        return new OpenVector3d(d0, d1, d2);
    }

    public OpenVector3d yRot(double value) {
        var f = Math.cos(value);
        var f1 = Math.sin(value);
        var d0 = x * f + z * f1;
        var d1 = y;
        var d2 = z * f - x * f1;
        return new OpenVector3d(d0, d1, d2);
    }

    public OpenVector3d zRot(double value) {
        var f = Math.cos(value);
        var f1 = Math.sin(value);
        var d0 = x * f + y * f1;
        var d1 = y * f - x * f1;
        var d2 = z;
        return new OpenVector3d(d0, d1, d2);
    }

//    public Vector3d align(EnumSet<OpenDirection.Axis> set) {
//        double d0 = set.contains(OpenDirection.Axis.X) ? Math.floor(x) : x;
//        double d1 = set.contains(OpenDirection.Axis.Y) ? Math.floor(y) : y;
//        double d2 = set.contains(OpenDirection.Axis.Z) ? Math.floor(z) : z;
//        return new Vector3d(d0, d1, d2);
//    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    public OpenVector3d copy() {
        return new OpenVector3d(x, y, z);
    }
}
