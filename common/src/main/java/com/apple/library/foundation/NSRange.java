package com.apple.library.foundation;

import java.util.Objects;

@SuppressWarnings("unused")
public class NSRange {

    public static final NSRange ZERO = new NSRange(0, 0);

    public final int location;
    public final int length;

    public NSRange(int location, int length) {
        this.location = location;
        this.length = length;
    }

    public static NSRange of(int startIndex, int endIndex) {
        int i = Math.min(startIndex, endIndex);
        int j = Math.max(startIndex, endIndex);
        return new NSRange(i, j - i);
    }

    public boolean contains(int index) {
        int diff = index - location;
        if (diff >= 0) {
            return diff <= length; // index in right side
        }
        return false;
    }

    public boolean intersects(NSRange range) {
        int diff = range.location - location;
        if (diff > 0) {
            return diff <= length; // range in right side
        }
        if (diff < 0) {
            return diff >= -range.length; // range in left side
        }
        return true;
    }

    public int startIndex() {
        return location;
    }

    public int endIndex() {
        return location + length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NSRange that)) return false;
        return location == that.location && length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, length);
    }

    @Override
    public String toString() {
        return String.format("%d ..< %d", startIndex(), endIndex());
    }
}
