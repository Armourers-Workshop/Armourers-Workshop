package com.apple.library.foundation;

import java.util.Objects;

@SuppressWarnings("unused")
public class NSTextRange {

    public final NSTextPosition start;
    public final NSTextPosition end;

    public NSTextRange(NSTextPosition cur) {
        this(cur, cur);
    }

    public NSTextRange(NSTextPosition start, NSTextPosition end) {
        this.start = start;
        this.end = end;
    }

    public boolean isEmpty() {
        return start.equals(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NSTextRange that)) return false;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
