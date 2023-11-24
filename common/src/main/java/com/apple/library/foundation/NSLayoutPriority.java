package com.apple.library.foundation;

public class NSLayoutPriority {

    public static final NSLayoutPriority REQUIRED = new NSLayoutPriority(1000);
    public static final NSLayoutPriority DEFAULT_HIGH = new NSLayoutPriority(750);
    public static final NSLayoutPriority DEFAULT_LOW = new NSLayoutPriority(250);

    protected final double value;

    public NSLayoutPriority(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }
}
