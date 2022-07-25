package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ITexturePos;

public class TexturePos implements ITexturePos {

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
}
