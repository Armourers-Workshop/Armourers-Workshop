package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;

import java.util.EnumSet;

@SuppressWarnings("unused")
public class Vector3d implements Position {

    public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);

    public final double x;
    public final double y;
    public final double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Position pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public static Vector3d fromRGB24(int rgb) {
        double d0 = (double) (rgb >> 16 & 255) / 255.0D;
        double d1 = (double) (rgb >> 8 & 255) / 255.0D;
        double d2 = (double) (rgb & 255) / 255.0D;
        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d atCenterOf(BlockPos pos) {
        return new Vector3d((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    public static Vector3d atLowerCornerOf(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vector3d atBottomCenterOf(BlockPos pos) {
        return new Vector3d((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D);
    }

    public static Vector3d upFromBottomCenterOf(BlockPos pos, double offset) {
        return new Vector3d((double) pos.getX() + 0.5D, (double) pos.getY() + offset, (double) pos.getZ() + 0.5D);
    }

    public static Vector3d directionFromRotation(float p_189986_0_, float p_189986_1_) {
        float f = MathUtils.cos(-p_189986_1_ * ((float) Math.PI / 180F) - (float) Math.PI);
        float f1 = MathUtils.sin(-p_189986_1_ * ((float) Math.PI / 180F) - (float) Math.PI);
        float f2 = -MathUtils.cos(-p_189986_0_ * ((float) Math.PI / 180F));
        float f3 = MathUtils.sin(-p_189986_0_ * ((float) Math.PI / 180F));
        return new Vector3d(f1 * f2, f3, f * f2);
    }

    public Vector3d vectorTo(Vector3d pos) {
        return new Vector3d(pos.x - this.x, pos.y - this.y, pos.z - this.z);
    }

    public Vector3d normalize() {
        double d0 = MathUtils.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? ZERO : new Vector3d(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dot(Vector3d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public Vector3d cross(Vector3d vec) {
        return new Vector3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Vector3d subtract(Vector3d delta) {
        return this.subtract(delta.x, delta.y, delta.z);
    }

    public Vector3d subtract(double tx, double ty, double tz) {
        return add(-tx, -ty, -tz);
    }

    public Vector3d add(Vector3d delta) {
        return this.add(delta.x, delta.y, delta.z);
    }

    public Vector3d add(double tx, double ty, double tz) {
        return new Vector3d(x + tx, y + ty, z + tz);
    }

    public boolean closerThan(Position pos, double p_237488_2_) {
        return distanceToSqr(pos.x(), pos.y(), pos.z()) < p_237488_2_ * p_237488_2_;
    }

    public double distanceTo(Vector3d pos) {
        double d0 = pos.x - x;
        double d1 = pos.y - y;
        double d2 = pos.z - z;
        return MathUtils.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceToSqr(Vector3d pos) {
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

    public Vector3d scale(double v) {
        return multiply(v, v, v);
    }

    public Vector3d reverse() {
        return scale(-1.0D);
    }

    public Vector3d multiply(Vector3d pos) {
        return multiply(pos.x, pos.y, pos.z);
    }

    public Vector3d multiply(double dx, double dy, double dz) {
        return new Vector3d(x * dx, y * dy, z * dz);
    }

    public double length() {
        return MathUtils.sqrt(x * x + y * y + z * z);
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector3d)) {
            return false;
        }
        Vector3d vector3d = (Vector3d) other;
        if (Double.compare(vector3d.x, x) != 0) {
            return false;
        }
        if (Double.compare(vector3d.y, y) != 0) {
            return false;
        }
        return Double.compare(vector3d.z, z) == 0;
    }

    @Override
    public int hashCode() {
        long j = Double.doubleToLongBits(x);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(z);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    public String toString() {
        return String.format("(%g %g %g)", x, y, z);
    }

    public Vector3d xRot(float p_178789_1_) {
        double f = MathUtils.cos(p_178789_1_);
        double f1 = MathUtils.sin(p_178789_1_);
        double d0 = x;
        double d1 = y * f + z * f1;
        double d2 = z * f - y * f1;
        return new Vector3d(d0, d1, d2);
    }

    public Vector3d yRot(float p_178785_1_) {
        double f = MathUtils.cos(p_178785_1_);
        double f1 = MathUtils.sin(p_178785_1_);
        double d0 = x * f + z * f1;
        double d1 = y;
        double d2 = z * f - x * f1;
        return new Vector3d(d0, d1, d2);
    }

//    public static Vector3d directionFromRotation(Vector2f p_189984_0_) {
//        return directionFromRotation(p_189984_0_.x, p_189984_0_.y);
//    }

    public Vector3d zRot(float p_242988_1_) {
        double f = MathUtils.cos(p_242988_1_);
        double f1 = MathUtils.sin(p_242988_1_);
        double d0 = x * f + y * f1;
        double d1 = y * f - x * f1;
        double d2 = z;
        return new Vector3d(d0, d1, d2);
    }

    public Vector3d align(EnumSet<Direction.Axis> set) {
        double d0 = set.contains(Direction.Axis.X) ? (double) MathUtils.floor(x) : x;
        double d1 = set.contains(Direction.Axis.Y) ? (double) MathUtils.floor(y) : y;
        double d2 = set.contains(Direction.Axis.Z) ? (double) MathUtils.floor(z) : z;
        return new Vector3d(d0, d1, d2);
    }

    public double get(Direction.Axis axis) {
        return axis.choose(x, y, z);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }
}
