package com.apple.library.uikit;

import java.util.Objects;

@SuppressWarnings("unused")
public class UIColor {

    public static final UIColor CLEAR = new UIColor(0x00000000, true);

    public static final UIColor WHITE = new UIColor(255, 255, 255);
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

    public UIColor(int rgba, boolean hasAlpha) {
        if (hasAlpha) {
            value = rgba;
        } else {
            value = 0xff000000 | rgba;
        }
    }

    public UIColor(int rgb) {
        value = 0xff000000 | rgb;
    }

    public static UIColor of(int rgb) {
        return new UIColor(rgb);
    }

    public static UIColor rgba(int rgba) {
        return new UIColor(rgba, true);
    }

    public static UIColor decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        return new UIColor((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    public static UIColor getHSBColor(float h, float s, float b) {
        return new UIColor(HSBtoRGB(h, s, b));
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }

    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else saturation = 0;
        if (saturation == 0) hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) hue = bluec - greenc;
            else if (g == cmax) hue = 2.0f + redc - bluec;
            else hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0) hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public int getRed() {
        return (getRGB() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (getRGB() >> 8) & 0xFF;
    }

    public int getBlue() {
        return (getRGB()) & 0xFF;
    }

    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
    }

    public int getRGB() {
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
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("#%08x", value);
    }
}
