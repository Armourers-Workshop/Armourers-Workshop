package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenRectangle2f {

    public final static OpenRectangle2f ZERO = new OpenRectangle2f();

    public float x;
    public float y;
    public float width;
    public float height;

    public OpenRectangle2f() {
    }

    public OpenRectangle2f(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public OpenRectangle2f(double x, double y, double width, double height) {
        this((float) x, (float) y, (float) width, (float) height);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    public float minX() {
        return this.x;
    }

    public float minY() {
        return this.y;
    }

    public float midX() {
        return this.x + this.width / 2;
    }

    public float midY() {
        return this.y + this.height / 2;
    }

    public float maxX() {
        return this.x + this.width;
    }

    public float maxY() {
        return this.y + this.height;
    }

    public OpenRectangle2f copy() {
        return new OpenRectangle2f(x, y, width, height);
    }

    public OpenRectangle2f scale(float s) {
        return new OpenRectangle2f(x * s, y * s, width * s, height * s);
    }

    public OpenRectangle2f offset(OpenVector2f point) {
        return offset(point.x(), point.y());
    }

    public OpenRectangle2f offset(float dx, float dy) {
        return new OpenRectangle2f(x + dx, y + dy, width, height);
    }

    public boolean contains(OpenVector2f point) {
        return contains(point.x(), point.y());
    }

    public boolean contains(float tx, float ty) {
        return x <= tx && tx <= (x + width) && y <= ty && ty <= (y + height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenRectangle2f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f; %f %f)", x, y, width, height);
    }
}
