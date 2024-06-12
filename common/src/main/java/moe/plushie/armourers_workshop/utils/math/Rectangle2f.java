package moe.plushie.armourers_workshop.utils.math;

import java.util.Objects;

@SuppressWarnings("unused")
public class Rectangle2f {

    public final static Rectangle2f ZERO = new Rectangle2f(0, 0, 0, 0);

    private float x;
    private float y;
    private float width;
    private float height;

    public Rectangle2f(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getMinX() {
        return this.x;
    }

    public float getMinY() {
        return this.y;
    }

    public float getMidX() {
        return this.x + this.width / 2;
    }

    public float getMidY() {
        return this.y + this.height / 2;
    }

    public float getMaxX() {
        return this.x + this.width;
    }

    public float getMaxY() {
        return this.y + this.height;
    }

    public Rectangle2f copy() {
        return new Rectangle2f(x, y, width, height);
    }

    public Rectangle2f scale(float s) {
        return new Rectangle2f(x * s, y * s, width * s, height * s);
    }

    public Rectangle2f offset(Vector2f point) {
        return offset((float) point.x(), (float) point.y());
    }

    public Rectangle2f offset(float dx, float dy) {
        return new Rectangle2f(x + dx, y + dy, width, height);
    }

    public boolean contains(Vector2f point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(float tx, float ty) {
        return x <= tx && tx <= (x + width) && y <= ty && ty <= (y + height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rectangle2f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return String.format("(%g %g; %g %g)", x, y, width, height);
    }
}
