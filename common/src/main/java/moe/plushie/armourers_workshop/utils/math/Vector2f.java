package moe.plushie.armourers_workshop.utils.math;

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

    public String toString() {
        return String.format("(%f %f)", x, y);
    }
}
