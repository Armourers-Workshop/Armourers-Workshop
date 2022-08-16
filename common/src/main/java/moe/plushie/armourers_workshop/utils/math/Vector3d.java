package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;

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

    public static Vector3d fromRGB24(int p_237487_0_) {
        double d0 = (double) (p_237487_0_ >> 16 & 255) / 255.0D;
        double d1 = (double) (p_237487_0_ >> 8 & 255) / 255.0D;
        double d2 = (double) (p_237487_0_ & 255) / 255.0D;
        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d atCenterOf(Vec3i pos) {
        return new Vector3d((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    public static Vector3d atLowerCornerOf(Vec3i pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vector3d atBottomCenterOf(Vec3i pos) {
        return new Vector3d((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D);
    }

    public static Vector3d upFromBottomCenterOf(Vec3i pos, double offset) {
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

    public double dot(Vector3d p_72430_1_) {
        return this.x * p_72430_1_.x + this.y * p_72430_1_.y + this.z * p_72430_1_.z;
    }

    public Vector3d cross(Vector3d p_72431_1_) {
        return new Vector3d(this.y * p_72431_1_.z - this.z * p_72431_1_.y, this.z * p_72431_1_.x - this.x * p_72431_1_.z, this.x * p_72431_1_.y - this.y * p_72431_1_.x);
    }

    public Vector3d subtract(Vector3d p_178788_1_) {
        return this.subtract(p_178788_1_.x, p_178788_1_.y, p_178788_1_.z);
    }

    public Vector3d subtract(double p_178786_1_, double p_178786_3_, double p_178786_5_) {
        return this.add(-p_178786_1_, -p_178786_3_, -p_178786_5_);
    }

    public Vector3d add(Vector3d p_178787_1_) {
        return this.add(p_178787_1_.x, p_178787_1_.y, p_178787_1_.z);
    }

    public Vector3d add(double p_72441_1_, double p_72441_3_, double p_72441_5_) {
        return new Vector3d(this.x + p_72441_1_, this.y + p_72441_3_, this.z + p_72441_5_);
    }

    public boolean closerThan(Position p_237488_1_, double p_237488_2_) {
        return this.distanceToSqr(p_237488_1_.x(), p_237488_1_.y(), p_237488_1_.z()) < p_237488_2_ * p_237488_2_;
    }

    public double distanceTo(Vector3d p_72438_1_) {
        double d0 = p_72438_1_.x - this.x;
        double d1 = p_72438_1_.y - this.y;
        double d2 = p_72438_1_.z - this.z;
        return MathUtils.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceToSqr(Vector3d pos) {
        double d0 = pos.x - this.x;
        double d1 = pos.y - this.y;
        double d2 = pos.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(double p_186679_1_, double p_186679_3_, double p_186679_5_) {
        double d0 = p_186679_1_ - this.x;
        double d1 = p_186679_3_ - this.y;
        double d2 = p_186679_5_ - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public Vector3d scale(double v) {
        return this.multiply(v, v, v);
    }

    public Vector3d reverse() {
        return this.scale(-1.0D);
    }

    public Vector3d multiply(Vector3d pos) {
        return this.multiply(pos.x, pos.y, pos.z);
    }

    public Vector3d multiply(double x, double y, double z) {
        return new Vector3d(this.x * x, this.y * y, this.z * z);
    }

    public double length() {
        return MathUtils.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Vector3d)) {
            return false;
        } else {
            Vector3d vector3d = (Vector3d) other;
            if (Double.compare(vector3d.x, this.x) != 0) {
                return false;
            } else if (Double.compare(vector3d.y, this.y) != 0) {
                return false;
            } else {
                return Double.compare(vector3d.z, this.z) == 0;
            }
        }
    }

    @Override
    public int hashCode() {
        long j = Double.doubleToLongBits(this.x);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.z);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vector3d xRot(float p_178789_1_) {
        float f = MathUtils.cos(p_178789_1_);
        float f1 = MathUtils.sin(p_178789_1_);
        double d0 = this.x;
        double d1 = this.y * (double) f + this.z * (double) f1;
        double d2 = this.z * (double) f - this.y * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    public Vector3d yRot(float p_178785_1_) {
        float f = MathUtils.cos(p_178785_1_);
        float f1 = MathUtils.sin(p_178785_1_);
        double d0 = this.x * (double) f + this.z * (double) f1;
        double d1 = this.y;
        double d2 = this.z * (double) f - this.x * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

//    public static Vector3d directionFromRotation(Vector2f p_189984_0_) {
//        return directionFromRotation(p_189984_0_.x, p_189984_0_.y);
//    }

    public Vector3d zRot(float p_242988_1_) {
        float f = MathUtils.cos(p_242988_1_);
        float f1 = MathUtils.sin(p_242988_1_);
        double d0 = this.x * (double) f + this.y * (double) f1;
        double d1 = this.y * (double) f - this.x * (double) f1;
        double d2 = this.z;
        return new Vector3d(d0, d1, d2);
    }

    public Vector3d align(EnumSet<Direction.Axis> set) {
        double d0 = set.contains(Direction.Axis.X) ? (double) MathUtils.floor(this.x) : this.x;
        double d1 = set.contains(Direction.Axis.Y) ? (double) MathUtils.floor(this.y) : this.y;
        double d2 = set.contains(Direction.Axis.Z) ? (double) MathUtils.floor(this.z) : this.z;
        return new Vector3d(d0, d1, d2);
    }

    public double get(Direction.Axis p_216370_1_) {
        return p_216370_1_.choose(this.x, this.y, this.z);
    }

    public final double x() {
        return this.x;
    }

    public final double y() {
        return this.y;
    }

    public final double z() {
        return this.z;
    }
}
