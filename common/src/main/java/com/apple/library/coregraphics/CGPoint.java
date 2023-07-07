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

    public void apply(CGAffineTransform t) {
        float tx = t.a * x + t.c * y + t.tx;
        float ty = t.b * x + t.d * y + t.ty;
        this.x = tx;
        this.y = ty;
    }

    public CGPoint applying(CGAffineTransform t) {
        CGPoint pos = copy();
        pos.apply(t);
        return pos;
    }

    public CGPoint copy() {
        return new CGPoint(x, y);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGPoint pos = (CGPoint) o;
        return Float.compare(pos.x, x) == 0 && Float.compare(pos.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", x, y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

