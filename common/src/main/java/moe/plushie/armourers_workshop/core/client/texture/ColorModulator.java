package moe.plushie.armourers_workshop.core.client.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.Colors;

public class ColorModulator {

    private static final Int2ObjectOpenHashMap<OpenVector4f> COLORS = new Int2ObjectOpenHashMap<>();

    public static OpenVector4f getColor(int color) {
        return COLORS.computeIfAbsent(color | 0xff000000, it -> {
            float red = Colors.getRed(it) / 255f;
            float green = Colors.getGreen(it) / 255f;
            float blue = Colors.getBlue(it) / 255f;
            return new OpenVector4f(red, green, blue, 1f);
        });
    }
}
