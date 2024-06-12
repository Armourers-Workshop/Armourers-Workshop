package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ITexturePos;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TexturePos that)) return false;
        return u == that.u && v == that.v;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", u, v);
    }
}
