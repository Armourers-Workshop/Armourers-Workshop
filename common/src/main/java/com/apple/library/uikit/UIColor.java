package com.apple.library.uikit;

@SuppressWarnings("unused")
public class UIColor {

    public static final UIColor CLEAR = new UIColor(0x00000000);

    public static final UIColor WHITE = new UIColor(0xffffffff);
    public static final UIColor LIGHT_GRAY = new UIColor(192, 192, 192);
    public static final UIColor GRAY = new UIColor(128, 128, 128);
    public static final UIColor DARK_GRAY = new UIColor(64, 64, 64);
    public static final UIColor BLACK = new UIColor(0, 0, 0);
    public static final UIColor RED = new UIColor(255, 0, 0);
    public static final UIColor PINK = new UIColor(255, 175, 175);
    public static final UIColor ORANGE = new UIColor(255, 200, 0);
    public static final UIColor YELLOW = new UIColor(255, 255, 0);
    public static final UIColor GREEN = new UIColor(0, 255, 0);
    public static final UIColor MAGENTA = new UIColor(255, 0, 255);
    public static final UIColor CYAN = new UIColor(0, 255, 255);
    public static final UIColor BLUE = new UIColor(0, 0, 255);

    private final int value;

    public UIColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public UIColor(int r, int g, int b, int a) {
        value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    public UIColor(int argb) {
        value = argb;
    }

    public static UIColor of(int argb) {
        return new UIColor(argb);
    }

    public static UIColor decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        return new UIColor((i >> 16) & 0xff, (i >> 8) & 0xff, i & 0xff);
    }

    public int red() {
        return (value >> 16) & 0xff;
    }

    public int green() {
        return (value >> 8) & 0xff;
    }

    public int blue() {
        return (value) & 0xff;
    }

    public int alpha() {
        return (value >> 24) & 0xff;
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIColor that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("#%08x", value);
    }
}
