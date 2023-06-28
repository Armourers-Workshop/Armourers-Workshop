package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public class Vector3i implements Comparable<Vector3i>, IVector3i, Position {

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

    public Vector3i(IVector3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector3i)) {
            return false;
        }
        Vector3i v = (Vector3i) other;
        if (this.getX() != v.getX()) {
            return false;
        } else if (this.getY() != v.getY()) {
            return false;
        } else {
            return this.getZ() == v.getZ();
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(Vector3i v) {
        if (this.getY() == v.getY()) {
            return this.getZ() == v.getZ() ? this.getX() - v.getX() : this.getZ() - v.getZ();
        } else {
            return this.getY() - v.getY();
        }
    }

    public BlockPos asBlockPos() {
        return new BlockPos(x, y, z);
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

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
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

    public Vector3i relative(Direction dir, int i) {
        return i == 0 ? this : new Vector3i(this.getX() + dir.getStepX() * i, this.getY() + dir.getStepY() * i, this.getZ() + dir.getStepZ() * i);
    }

    public Vector3i cross(Vector3i pos) {
        return new Vector3i(this.getY() * pos.getZ() - this.getZ() * pos.getY(), this.getZ() * pos.getX() - this.getX() * pos.getZ(), this.getX() * pos.getY() - this.getY() * pos.getX());
    }

    public boolean closerThan(Vector3i pos, double d) {
        return this.distSqr(pos.getX(), pos.getY(), pos.getZ(), false) < d * d;
    }

    public boolean closerThan(Position v, double f) {
        return this.distSqr(v.x(), v.y(), v.z(), true) < f * f;
    }

    public double distSqr(Vector3i v) {
        return this.distSqr(v.getX(), v.getY(), v.getZ(), true);
    }

    public double distSqr(Position v, boolean fl) {
        return this.distSqr(v.x(), v.y(), v.z(), fl);
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

    public int get(Direction.Axis axis) {
        return axis.choose(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return String.format("(%d %d %d)", x, y, z);
    }
}
