package moe.plushie.armourers_workshop.core.data.color;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

public class PaintColor implements IPaintColor {

    public final static PaintColor CLEAR = new PaintColor(0, 0, SkinPaintTypes.NONE);
    public final static PaintColor WHITE = new PaintColor(-1, -1, SkinPaintTypes.NORMAL);
    // we need an object pool to reduce color object
    private final static Cache<Integer, PaintColor> POOL = CacheBuilder.newBuilder()
            .maximumSize(2048)
            .build();
    public static final Codec<IPaintColor> CODEC = Codec.INT.xmap(PaintColor::of, IPaintColor::getRawValue);
    private final int value;
    private final int rgb;
    private final ISkinPaintType paintType;

//    private PaintColor(int rgb, ISkinPaintType paintType) {
//        this((rgb & 0xffffff) | ((paintType.getId() & 0xff) << 24), rgb, paintType);
//    }

    protected PaintColor(int value, int rgb, ISkinPaintType paintType) {
        this.value = value;
        this.paintType = paintType;
        this.rgb = rgb;
    }

    public static PaintColor of(int value) {
        if (value == 0) {
            return CLEAR;
        }
        return of(value, getPaintType(value));
    }

    public static PaintColor of(int r, int g, int b, ISkinPaintType paintType) {
        return of(r << 16 | g << 8 | b, paintType);
    }

    public static PaintColor of(int rgb, ISkinPaintType paintType) {
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

    public static boolean isOpaque(int color) {
        return (color & 0xff000000) != 0;
    }

    public boolean isEmpty() {
        return getPaintType() == SkinPaintTypes.NONE;
    }

    @Override
    public int getRed() {
        return (rgb >> 16) & 0xff;
    }

    @Override
    public int getGreen() {
        return (rgb >> 8) & 0xff;
    }

    @Override
    public int getBlue() {
        return rgb & 0xff;
    }

    @Override
    public int getRGB() {
        return rgb;
    }

    @Override
    public int getRawValue() {
        return value;
    }

    @Override
    public ISkinPaintType getPaintType() {
        return paintType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaintColor that)) return false;
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
