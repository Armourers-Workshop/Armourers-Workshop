package moe.plushie.armourers_workshop.utils.math;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;

import java.util.List;
import java.util.Objects;

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

    public Vector3i(List<Integer> values) {
        this(values[0], values[1], values[2]);
    }


    @Override
    public int compareTo(Vector3i v) {
        int dy = getY() - v.getY();
        if (dy != 0) {
            return dy;
        }
        int dz = getZ() - v.getZ();
        if (dz != 0) {
            return dz;
        }
        return getX() - v.getX();
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

    public int get(Direction.Axis axis) {
        return axis.choose(x, y, z);
    }

    public Vector3i above() {
        return above(1);
    }

    public Vector3i above(int step) {
        return relative(Direction.UP, step);
    }

    public Vector3i below() {
        return below(1);
    }

    public Vector3i below(int step) {
        return relative(Direction.DOWN, step);
    }

    public Vector3i relative(Direction dir, int i) {
        if (i == 0) {
            return this;
        }
        return new Vector3i(getX() + dir.getStepX() * i, getY() + dir.getStepY() * i, getZ() + dir.getStepZ() * i);
    }

    public Vector3i cross(Vector3i pos) {
        return new Vector3i(getY() * pos.getZ() - getZ() * pos.getY(), getZ() * pos.getX() - getX() * pos.getZ(), getX() * pos.getY() - getY() * pos.getX());
    }

    public boolean closerThan(Vector3i pos, double d) {
        return distSqr(pos.getX(), pos.getY(), pos.getZ(), false) < d * d;
    }

    public boolean closerThan(Position v, double f) {
        return distSqr(v.x(), v.y(), v.z(), true) < f * f;
    }

    public double distSqr(Vector3i v) {
        return distSqr(v.getX(), v.getY(), v.getZ(), true);
    }

    public double distSqr(Position v, boolean fl) {
        return distSqr(v.x(), v.y(), v.z(), fl);
    }

    public double distSqr(double tx, double ty, double tz, boolean p_218140_7_) {
        double d0 = p_218140_7_ ? 0.5D : 0.0D;
        double d1 = (double) getX() + d0 - tx;
        double d2 = (double) getY() + d0 - ty;
        double d3 = (double) getZ() + d0 - tz;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    public int distManhattan(Vector3i pos) {
        float f = (float) Math.abs(pos.getX() - getX());
        float f1 = (float) Math.abs(pos.getY() - getY());
        float f2 = (float) Math.abs(pos.getZ() - getZ());
        return (int) (f + f1 + f2);
    }

    public List<Integer> toList() {
        return Lists.newArrayList(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3i that)) return false;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%d %d %d)", x, y, z);
    }
}
