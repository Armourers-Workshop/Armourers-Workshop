package com.apple.library.coregraphics;

import java.util.Objects;

public class CGPoint {

    public static final CGPoint ZERO = new CGPoint(0, 0);

    public int x;
    public int y;

    public CGPoint(int x, int y) {
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

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", x, y);
    }
}

