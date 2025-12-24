package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenRectangle2i {

    public final static OpenRectangle2i ZERO = new OpenRectangle2i(0, 0, 0, 0);

    public int x;
    public int y;
    public int width;
    public int height;

    public OpenRectangle2i() {
    }

    public OpenRectangle2i(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public OpenRectangle2i(float x, float y, float width, float height) {
        this(OpenMath.floori(x), OpenMath.floori(y), OpenMath.floori(width), OpenMath.floori(height));
    }


    public void set(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int minX() {
        return this.x;
    }

    public int minY() {
        return this.y;
    }

    public int midX() {
        return this.x + this.width / 2;
    }

    public int midY() {
        return this.y + this.height / 2;
    }

    public int maxX() {
        return this.x + this.width;
    }

    public int maxY() {
        return this.y + this.height;
    }

    public OpenRectangle2i copy() {
        return new OpenRectangle2i(x, y, width, height);
    }

    public OpenRectangle2i scale(float s) {
        return new OpenRectangle2i(x * s, y * s, width * s, height * s);
    }

    public OpenRectangle2i offset(OpenVector2i point) {
        return offset(point.x(), point.y());
    }

    public OpenRectangle2i offset(int dx, int dy) {
        return new OpenRectangle2i(x + dx, y + dy, width, height);
    }

    public boolean contains(OpenVector2i point) {
        return contains(point.x(), point.y());
    }

    public boolean contains(int tx, int ty) {
        return x <= tx && tx <= (x + width) && y <= ty && ty <= (y + height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenRectangle2i that)) return false;
        return x == that.x && y == that.y && width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%d %d; %d %d)", x, y, width, height);
    }
}
