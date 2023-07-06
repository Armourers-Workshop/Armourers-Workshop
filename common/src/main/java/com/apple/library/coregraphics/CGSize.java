package com.apple.library.coregraphics;

public class CGSize {

    public static final CGSize ZERO = new CGSize(0, 0);

    public float width;
    public float height;

    public CGSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", width, height);
    }
}
