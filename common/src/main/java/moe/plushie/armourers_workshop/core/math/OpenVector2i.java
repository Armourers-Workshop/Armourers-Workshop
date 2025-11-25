package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IVector2i;
import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenVector2i implements IVector2i {

    public static final OpenVector2i ZERO = new OpenVector2i(0, 0);

    public int x;
    public int y;

    public OpenVector2i() {
        this(0, 0);
    }

    public OpenVector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public OpenVector2i(double x, double y) {
        this(OpenMath.floori(x), OpenMath.floori(y));
    }

    public void add(int tx, int ty) {
        x += tx;
        y += ty;
    }

    public void add(OpenVector2i pos) {
        x += pos.x;
        y += pos.y;
    }

    public void subtract(int tx, int ty) {
        x -= tx;
        y -= ty;
    }

    public void subtract(OpenVector2i pos) {
        x -= pos.x;
        y -= pos.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector2i that)) return false;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%d %d)", x, y);
    }
}
