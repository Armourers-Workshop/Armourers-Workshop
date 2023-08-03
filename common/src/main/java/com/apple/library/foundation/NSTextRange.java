package com.apple.library.foundation;

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
}
