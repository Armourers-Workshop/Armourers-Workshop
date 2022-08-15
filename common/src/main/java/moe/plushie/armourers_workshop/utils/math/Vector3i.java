package moe.plushie.armourers_workshop.utils.math;

import com.google.common.base.MoreObjects;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public class Vector3i implements Comparable<Vector3i>, Position, IVector3i {

    public static final Vector3i ZERO = new Vector3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i(double x, double y, double z) {
        this(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
    }

    public Vector3i(Position pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Vector3i)) {
            return false;
        } else {
            Vector3i vector3i = (Vector3i) other;
            if (this.getX() != vector3i.getX()) {
                return false;
            } else if (this.getY() != vector3i.getY()) {
                return false;
            } else {
                return this.getZ() == vector3i.getZ();
            }
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(Vector3i p_compareTo_1_) {
        if (this.getY() == p_compareTo_1_.getY()) {
            return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
        } else {
            return this.getY() - p_compareTo_1_.getY();
        }
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public int getX() {
        return this.x;
    }

    protected void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    protected void setY(int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    protected void setZ(int z) {
        this.z = z;
    }

    public Vector3i above() {
        return this.above(1);
    }

    public Vector3i above(int p_177981_1_) {
        return this.relative(Direction.UP, p_177981_1_);
    }

    public Vector3i below() {
        return this.below(1);
    }

    public Vector3i below(int p_177979_1_) {
        return this.relative(Direction.DOWN, p_177979_1_);
    }

    public Vector3i relative(Direction p_177967_1_, int p_177967_2_) {
        return p_177967_2_ == 0 ? this : new Vector3i(this.getX() + p_177967_1_.getStepX() * p_177967_2_, this.getY() + p_177967_1_.getStepY() * p_177967_2_, this.getZ() + p_177967_1_.getStepZ() * p_177967_2_);
    }

    public Vector3i cross(Vector3i p_177955_1_) {
        return new Vector3i(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
    }

    public boolean closerThan(Vector3i p_218141_1_, double p_218141_2_) {
        return this.distSqr(p_218141_1_.getX(), p_218141_1_.getY(), p_218141_1_.getZ(), false) < p_218141_2_ * p_218141_2_;
    }

    public boolean closerThan(Position p_218137_1_, double p_218137_2_) {
        return this.distSqr(p_218137_1_.x(), p_218137_1_.y(), p_218137_1_.z(), true) < p_218137_2_ * p_218137_2_;
    }

    public double distSqr(Vector3i p_177951_1_) {
        return this.distSqr(p_177951_1_.getX(), p_177951_1_.getY(), p_177951_1_.getZ(), true);
    }

    public double distSqr(Position p_218138_1_, boolean p_218138_2_) {
        return this.distSqr(p_218138_1_.x(), p_218138_1_.y(), p_218138_1_.z(), p_218138_2_);
    }

    public double distSqr(double p_218140_1_, double p_218140_3_, double p_218140_5_, boolean p_218140_7_) {
        double d0 = p_218140_7_ ? 0.5D : 0.0D;
        double d1 = (double) this.getX() + d0 - p_218140_1_;
        double d2 = (double) this.getY() + d0 - p_218140_3_;
        double d3 = (double) this.getZ() + d0 - p_218140_5_;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    public int distManhattan(Vector3i pos) {
        float f = (float) Math.abs(pos.getX() - this.getX());
        float f1 = (float) Math.abs(pos.getY() - this.getY());
        float f2 = (float) Math.abs(pos.getZ() - this.getZ());
        return (int) (f + f1 + f2);
    }

    public int get(Direction.Axis p_243648_1_) {
        return p_243648_1_.choose(this.x, this.y, this.z);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String toShortString() {
        return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
    }
}