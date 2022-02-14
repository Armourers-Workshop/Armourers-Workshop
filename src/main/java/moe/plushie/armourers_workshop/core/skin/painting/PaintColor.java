package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;

public class PaintColor {

    public final static PaintColor CLEAR = new PaintColor(0);

    private final int value;
    private final int argb;
    private final ISkinPaintType paintType;

    public PaintColor(int value) {
        this.value = value;
        this.paintType = getPaintType(value);
        this.argb = 0xff000000 | value;
    }

    public PaintColor(int argb, ISkinPaintType paintType) {
        this.value = (argb & 0xffffff) | ((paintType.getId() & 0xff) << 24);
        this.paintType = paintType;
        this.argb = argb;
    }

    public static ISkinPaintType getPaintType(int value) {
        return SkinPaintTypes.byId(value >> 24 & 0xff);
    }

    public boolean isEmpty() {
        return getPaintType() == SkinPaintTypes.NONE;
    }

    public int getRed() {
        return (argb >> 16) & 0xff;
    }

    public int getGreen() {
        return (argb >> 8) & 0xff;
    }

    public int getBlue() {
        return argb & 0xff;
    }

    public int getAlpha() {
        return (argb >> 24) & 0xff;
    }

    public int getRGB() {
        return argb;
    }

    public int getValue() {
        return value;
    }

    public ISkinPaintType getPaintType() {
        return paintType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value == ((PaintColor) o).value;
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
