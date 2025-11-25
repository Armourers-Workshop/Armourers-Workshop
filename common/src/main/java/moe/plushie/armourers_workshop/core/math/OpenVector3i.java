package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.List;

@SuppressWarnings("unused")
public class OpenVector3i implements Comparable<OpenVector3i>, IVector3i {

    public static final OpenVector3i ZERO = new OpenVector3i(0, 0, 0);

    public int x;
    public int y;
    public int z;

    public static final IDataCodec<OpenVector3i> CODEC = IDataCodec.INT.listOf().xmap(OpenVector3i::new, OpenVector3i::toList);


    public OpenVector3i() {
        this(0, 0, 0);
    }
    public OpenVector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public OpenVector3i(double x, double y, double z) {
        this(OpenMath.floori(x), OpenMath.floori(y), OpenMath.floori(z));
    }

    public OpenVector3i(IVector3f pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3i(IVector3i pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public OpenVector3i(List<Integer> values) {
        this(values.get(0), values.get(1), values.get(2));
    }

    @Override
    public int compareTo(OpenVector3i v) {
        int dy = y() - v.y();
        if (dy != 0) {
            return dy;
        }
        int dz = z() - v.z();
        if (dz != 0) {
            return dz;
        }
        return x() - v.x();
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int z() {
        return this.z;
    }

    public OpenVector3i above() {
        return above(1);
    }

    public OpenVector3i above(int step) {
        return relative(OpenDirection.UP, step);
    }

    public OpenVector3i below() {
        return below(1);
    }

    public OpenVector3i below(int step) {
        return relative(OpenDirection.DOWN, step);
    }

    public OpenVector3i relative(OpenDirection dir, int i) {
        if (i == 0) {
            return this;
        }
        return new OpenVector3i(x() + dir.stepX() * i, y() + dir.stepY() * i, z() + dir.stepZ() * i);
    }

    public OpenVector3i cross(OpenVector3i pos) {
        return new OpenVector3i(y() * pos.z() - z() * pos.y(), z() * pos.x() - x() * pos.z(), x() * pos.y() - y() * pos.x());
    }

    public boolean closerThan(OpenVector3i pos, double d) {
        return distSqr(pos.x(), pos.y(), pos.z(), false) < d * d;
    }

    public double distSqr(OpenVector3i v) {
        return distSqr(v.x(), v.y(), v.z(), true);
    }

    public double distSqr(double tx, double ty, double tz, boolean p_218140_7_) {
        double d0 = p_218140_7_ ? 0.5D : 0.0D;
        double d1 = (double) x() + d0 - tx;
        double d2 = (double) y() + d0 - ty;
        double d3 = (double) z() + d0 - tz;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    public int distManhattan(OpenVector3i pos) {
        float f = (float) Math.abs(pos.x() - x());
        float f1 = (float) Math.abs(pos.y() - y());
        float f2 = (float) Math.abs(pos.z() - z());
        return (int) (f + f1 + f2);
    }

    public List<Integer> toList() {
        return Collections.newList(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector3i that)) return false;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%d %d %d)", x, y, z);
    }
}
