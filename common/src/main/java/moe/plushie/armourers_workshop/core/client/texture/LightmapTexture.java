package moe.plushie.armourers_workshop.core.client.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;

public class LightmapTexture {

    private static final Int2ObjectOpenHashMap<OpenMatrix4f> TEXTURE_MATRICES = new Int2ObjectOpenHashMap<>();

    public static final int DEFAULT = 0xf000f0;

    public static int getU(int value) {
        return value & 0xffff;
    }

    public static int getV(int value) {
        return value >> 16 & 0xffff;
    }

    public static OpenMatrix4f getTextureMatrix(int lightmap, boolean isEmissive) {
        // we specified the fully lighting when create the vertex,
        // so we don't need any change when growing is required.
        if (isEmissive) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return TEXTURE_MATRICES.computeIfAbsent(lightmap, it -> {
            var newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.setTranslation(getU(it), getV(it), 0);
            return newValue;
        });
    }
}
