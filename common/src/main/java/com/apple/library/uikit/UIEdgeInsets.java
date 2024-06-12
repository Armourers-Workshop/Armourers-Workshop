package com.apple.library.uikit;

import java.util.Objects;

@SuppressWarnings("unused")
public class UIEdgeInsets {

    public static final UIEdgeInsets ZERO = new UIEdgeInsets(0, 0, 0, 0);

    public final float top;
    public final float left;
    public final float right;
    public final float bottom;

    public UIEdgeInsets(UIEdgeInsets edge) {
        this(edge.top, edge.left, edge.bottom, edge.right);
    }

    public UIEdgeInsets(float top, float left, float bottom, float right) {
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIEdgeInsets that)) return false;
        return top == that.top && left == that.left && right == that.right && bottom == that.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, right, bottom);
    }

    @Override
    public String toString() {
        return String.format("(%f %f %f %f)", top, left, bottom, right);
    }
}
