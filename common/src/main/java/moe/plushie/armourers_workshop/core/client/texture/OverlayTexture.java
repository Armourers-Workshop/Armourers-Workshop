package moe.plushie.armourers_workshop.core.client.texture;

public class OverlayTexture {

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
}
