package moe.plushie.armourers_workshop.core.client.texture;

public class LightmapTexture {

    public static final int DEFAULT = 0xf000f0;

    public static int getU(int value) {
        return value & 0xffff;
    }

    public static int getV(int value) {
        return value >> 16 & 0xffff;
    }
}
