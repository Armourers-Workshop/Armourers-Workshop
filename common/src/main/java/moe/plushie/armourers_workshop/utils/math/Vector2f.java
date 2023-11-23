package moe.plushie.armourers_workshop.utils.math;

import java.util.Objects;

@SuppressWarnings("unused")
public class Vector2f {

    public static Vector2f ZERO = new Vector2f();

    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(double x, double y) {
        this((float) x, (float) y);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2f vector2f)) return false;
        return Float.compare(vector2f.x, x) == 0 && Float.compare(vector2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return String.format("(%g %g)", x, y);
    }
}
