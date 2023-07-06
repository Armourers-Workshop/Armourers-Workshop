package com.apple.library.coregraphics;

import java.util.Objects;

public class CGPoint {

    public static final CGPoint ZERO = new CGPoint(0, 0);

    public float x;
    public float y;

    public CGPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CGPoint)) return false;
        CGPoint point2i = (CGPoint) o;
        return x == point2i.x && y == point2i.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", x, y);
    }
}

