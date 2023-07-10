package com.apple.library.coregraphics;

import com.apple.library.impl.InterpolableImpl;

import java.util.Objects;

public class CGSize implements InterpolableImpl<CGSize> {

    public static final CGSize ZERO = new CGSize(0, 0);

    public float width;
    public float height;

    public CGSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void apply(CGAffineTransform t) {
        float w = t.a * width + t.c * height;
        float h = t.b * width + t.d * height;
        this.width = w;
        this.height = h;
    }

    public CGSize applying(CGAffineTransform t) {
        CGSize size = copy();
        size.apply(t);
        return size;
    }

    public CGSize copy() {
        return new CGSize(width, height);
    }

    @Override
    public CGSize interpolating(CGSize in, float t) {
        if (t <= 0) {
            return this;
        }
        if (t >= 1) {
            return in;
        }
        float v = 1 - t;
        float w = v * this.width + t * in.width;
        float h = v * this.height + t * in.height;
        return new CGSize(w, h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGSize size = (CGSize) o;
        return Float.compare(size.width, width) == 0 && Float.compare(size.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", width, height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
