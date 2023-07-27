package com.apple.library.foundation;

import java.util.Objects;

public class NSTextPosition {

    public static NSTextPosition ZERO = forward(0);

    public final int value;
    public final boolean isBackward;

    private NSTextPosition(int index, boolean isBackward) {
        this.value = index;
        this.isBackward = isBackward;
    }

    public static NSTextPosition forward(int index) {
        return new NSTextPosition(index, false);
    }

    public static NSTextPosition backward(int index) {
        return new NSTextPosition(index, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NSTextPosition)) return false;
        NSTextPosition that = (NSTextPosition) o;
        return value == that.value && isBackward == that.isBackward;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isBackward);
    }
}
