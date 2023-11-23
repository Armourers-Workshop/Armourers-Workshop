package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TextureKey implements ITextureKey {

    protected final float u;
    protected final float v;
    protected final float width;
    protected final float height;
    protected final float totalWidth;
    protected final float totalHeight;
    protected final ITextureProvider provider;


    public TextureKey(float u, float v, float width, float height, ITextureProvider provider) {
        this(u, v, width, height, provider.getWidth(), provider.getHeight(), provider);
    }

    public TextureKey(float u, float v, float width, float height, float totalWidth, float totalHeight) {
        this(u, v, width, height, totalWidth, totalHeight, null);
    }

    public TextureKey(float u, float v, float width, float height, float totalWidth, float totalHeight, ITextureProvider provider) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.provider = provider;
    }

    @Override
    public boolean isMirror() {
        return false;
    }

    @Override
    public float getU() {
        return u;
    }

    @Override
    public float getV() {
        return v;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getTotalWidth() {
        return totalWidth;
    }

    @Override
    public float getTotalHeight() {
        return totalHeight;
    }

    @Nullable
    @Override
    public ITextureProvider getProvider() {
        return provider;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextureKey that)) return false;
        return Float.compare(that.u, u) == 0 && Float.compare(that.v, v) == 0 && Float.compare(that.width, width) == 0 && Float.compare(that.height, height) == 0 && Float.compare(that.totalWidth, totalWidth) == 0 && Float.compare(that.totalHeight, totalHeight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, width, height, totalWidth, totalHeight);
    }
}
