package com.apple.library.coregraphics;

public class CGSize {

    public static final CGSize ZERO = new CGSize(0, 0);

    public int width;
    public int height;

    public CGSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", width, height);
    }
}
