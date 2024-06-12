package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IVector2f;

import java.util.Objects;

@SuppressWarnings("unused")
public class Vector2f implements IVector2f {

    public static Vector2f ZERO = new Vector2f();

    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(IVector2f value) {
        this(value.getX(), value.getY());
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(double x, double y) {
        this((float) x, (float) y);
    }

    public static Vector2f of(long value) {
        float p1 = Float.intBitsToFloat((int) value);
        float p2 = Float.intBitsToFloat((int) (value >> 32));
        return new Vector2f(p1, p2);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public float getX() {
        return this.x;
    }

    protected void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    protected void setY(float y) {
        this.y = y;
    }

    public long asLong() {
        int p1 = Float.floatToRawIntBits(x);
        int p2 = Float.floatToRawIntBits(y);
        return ((long) p2 << 32) | p1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2f that)) return false;
        return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return String.format("(%g %g)", x, y);
    }
}
