package com.apple.library.uikit;

import java.util.Objects;

public class UIEdgeInsets {

    public static final UIEdgeInsets ZERO = new UIEdgeInsets(0, 0, 0, 0);

    public final int top;
    public final int left;
    public final int right;
    public final int bottom;

    public UIEdgeInsets(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIEdgeInsets)) return false;
        UIEdgeInsets that = (UIEdgeInsets) o;
        return top == that.top && left == that.left && right == that.right && bottom == that.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, right, bottom);
    }

    @Override
    public String toString() {
        return String.format("(%d %d %d %d)", top, left, bottom, right);
    }
}
