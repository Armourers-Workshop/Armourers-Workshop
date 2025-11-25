package moe.plushie.armourers_workshop.core.client.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;

public class OverlayTexture {

    private static final Int2ObjectOpenHashMap<OpenMatrix4f> TEXTURE_MATRICES = new Int2ObjectOpenHashMap<>();

    public static final int NO_OVERLAY = 0x0a0000;

    public static int pack(int u, int v) {
        return u | v << 16;
    }

    public static int pack(float f, boolean bl) {
        var u = (int) (f * 15.0f);
        var v = bl ? 3 : 10;
        return pack(u, v);
    }

    public static int getU(int value) {
        return value & 0xffff;
    }

    public static int getV(int value) {
        return value >> 16 & 0xffff;
    }

    public static OpenMatrix4f getTextureMatrix(int overlay) {
        // We specified the no overlay when create the vertex,
        // so we don't need any change when no overlay is required.
        if (overlay == OverlayTexture.NO_OVERLAY) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return TEXTURE_MATRICES.computeIfAbsent(overlay, it -> {
            var newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.setTranslation(getU(it), getV(it), 0);
            return newValue;
        });
    }
}
