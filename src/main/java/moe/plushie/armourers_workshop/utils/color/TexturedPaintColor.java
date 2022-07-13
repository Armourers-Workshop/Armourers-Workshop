package moe.plushie.armourers_workshop.utils.color;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;

public class TexturedPaintColor extends PaintColor {

    // we need an object pool to reduce color object
    private final static Cache<Integer, TexturedPaintColor> POOL = CacheBuilder.newBuilder()
            .maximumSize(2048)
            .build();

    protected TexturedPaintColor(int value, int rgb, ISkinPaintType paintType) {
        super(value, rgb, paintType);
    }

    public static PaintColor of(int value) {
        if (value == 0) {
            return CLEAR;
        }
        return of(value, getPaintType(value));
    }

    public static PaintColor of(int rgb, ISkinPaintType paintType) {
        int value = (rgb & 0xffffff) | ((paintType.getId() & 0xff) << 24);
        TexturedPaintColor paintColor = POOL.getIfPresent(value);
        if (paintColor == null) {
            paintColor = new TexturedPaintColor(value, rgb, paintType);
            POOL.put(value, paintColor);
        }
        return paintColor;
    }
}
