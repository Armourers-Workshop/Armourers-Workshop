package moe.plushie.armourers_workshop.core.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

public class PaintColor {

    public final static PaintColor CLEAR = new PaintColor(0, 0, SkinPaintTypes.NONE);

    // we need an object pool to reduce color object
    private final static Cache<Integer, PaintColor> POOL = CacheBuilder.newBuilder()
            .maximumSize(2048)
            .build();

    private final int value;
    private final int rgb;
    private final ISkinPaintType paintType;

    private PaintColor(int value, int rgb, ISkinPaintType paintType) {
        this.value = value;
        this.paintType = paintType;
        this.rgb = rgb;
    }

    public static PaintColor of(int value) {
        return of(value, getPaintType(value));
    }

    public static PaintColor of(int rgb, ISkinPaintType paintType) {
        if (paintType == SkinPaintTypes.NONE) {
            return CLEAR;
        }
        int value = (rgb & 0xffffff) | ((paintType.getId() & 0xff) << 24);
        PaintColor paintColor = POOL.getIfPresent(value);
        if (paintColor == null) {
            paintColor = new PaintColor(value, rgb, paintType);
            POOL.put(value, paintColor);
        }
        return paintColor;
    }

    public static ISkinPaintType getPaintType(int value) {
        return SkinPaintTypes.byId(value >> 24 & 0xff);
    }

    public boolean isEmpty() {
        return getPaintType() == SkinPaintTypes.NONE;
    }

    public int getRed() {
        return (rgb >> 16) & 0xff;
    }

    public int getGreen() {
        return (rgb >> 8) & 0xff;
    }

    public int getBlue() {
        return rgb & 0xff;
    }

    public int getRGB() {
        return rgb;
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
