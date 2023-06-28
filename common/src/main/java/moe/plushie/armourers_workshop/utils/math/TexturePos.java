package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ITexturePos;

@SuppressWarnings("unused")
public class TexturePos implements ITexturePos {

    public static final TexturePos ZERO = new TexturePos(0, 0);

    public final int u;
    public final int v;

    public TexturePos(int u, int v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public int getU() {
        return this.u;
    }

    @Override
    public int getV() {
        return this.v;
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", u, v);
    }
}
