package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.core.BlockPos;

@SuppressWarnings("unused")
public class OpenVector3d {

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

    public static OpenVector3d fromRGB24(int rgb) {
        double d0 = (double) (rgb >> 16 & 255) / 255.0D;
        double d1 = (double) (rgb >> 8 & 255) / 255.0D;
        double d2 = (double) (rgb & 255) / 255.0D;
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
        double f = Math.cos(-b * (Math.PI / 180.0) - Math.PI);
        double f1 = Math.sin(-b * (Math.PI / 180.0) - Math.PI);
        double f2 = -Math.cos(-a * (Math.PI / 180.0));
        double f3 = Math.sin(-a * (Math.PI / 180.0));
        return new OpenVector3d(f1 * f2, f3, f * f2);
    }

    public OpenVector3d vectorTo(OpenVector3d pos) {
        return new OpenVector3d(pos.x - this.x, pos.y - this.y, pos.z - this.z);
    }

    public OpenVector3d normalize() {
        double d0 = Math.sqrt(x * x + y * y + z * z);
        return d0 < 1.0E-4D ? ZERO : new OpenVector3d(x / d0, y / d0, z / d0);
    }

    public double dot(OpenVector3d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public OpenVector3d cross(OpenVector3d vec) {
        return new OpenVector3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public OpenVector3d subtract(OpenVector3d delta) {
        return this.subtract(delta.x, delta.y, delta.z);
    }

    public OpenVector3d subtract(double tx, double ty, double tz) {
        return add(-tx, -ty, -tz);
    }

    public OpenVector3d add(OpenVector3d delta) {
        return this.add(delta.x, delta.y, delta.z);
    }

    public OpenVector3d add(double tx, double ty, double tz) {
        return new OpenVector3d(x + tx, y + ty, z + tz);
    }

    public double distanceTo(OpenVector3d pos) {
        double d0 = pos.x - x;
        double d1 = pos.y - y;
        double d2 = pos.z - z;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceToSqr(OpenVector3d pos) {
        double d0 = pos.x - x;
        double d1 = pos.y - y;
        double d2 = pos.z - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(double tx, double ty, double tz) {
        double d0 = tx - x;
        double d1 = ty - y;
        double d2 = tz - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public OpenVector3d scale(double v) {
        return multiply(v, v, v);
    }

    public OpenVector3d reverse() {
        return scale(-1.0D);
    }

    public OpenVector3d multiply(OpenVector3d pos) {
        return multiply(pos.x, pos.y, pos.z);
    }

    public OpenVector3d multiply(double dx, double dy, double dz) {
        return new OpenVector3d(x * dx, y * dy, z * dz);
    }

    public double length() {
        return OpenMath.sqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
    }

//    public void normalize() {
//        var scalar = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, z * z)));
//        this.x *= scalar;
//        this.y *= scalar;
//        this.z *= scalar;
//    }

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

    public OpenVector3d xRot(float value) {
        double f = Math.cos(value);
        double f1 = Math.sin(value);
        double d0 = x;
        double d1 = y * f + z * f1;
        double d2 = z * f - y * f1;
        return new OpenVector3d(d0, d1, d2);
    }

    public OpenVector3d yRot(float p_178785_1_) {
        double f = Math.cos(p_178785_1_);
        double f1 = Math.sin(p_178785_1_);
        double d0 = x * f + z * f1;
        double d1 = y;
        double d2 = z * f - x * f1;
        return new OpenVector3d(d0, d1, d2);
    }

    public OpenVector3d zRot(float p_242988_1_) {
        double f = Math.cos(p_242988_1_);
        double f1 = Math.sin(p_242988_1_);
        double d0 = x * f + y * f1;
        double d1 = y * f - x * f1;
        double d2 = z;
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

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public OpenVector3d copy() {
        return new OpenVector3d(x, y, z);
    }
}
