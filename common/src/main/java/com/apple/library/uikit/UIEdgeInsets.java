package com.apple.library.uikit;

import java.util.Objects;

public class UIEdgeInsets {

    public static final UIEdgeInsets ZERO = new UIEdgeInsets(0, 0, 0, 0);

    public final float top;
    public final float left;
    public final float right;
    public final float bottom;

    public UIEdgeInsets(float top, float left, float bottom, float right) {
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
