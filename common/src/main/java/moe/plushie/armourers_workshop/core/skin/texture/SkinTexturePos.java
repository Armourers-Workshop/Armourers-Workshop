package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinTexturePos;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

public class SkinTexturePos implements ISkinTexturePos {

    public static final SkinTexturePos DEFAULT = new SkinTexturePos(0, 0, 1, 1, 256, 256);

    protected final float u;
    protected final float v;
    protected final float width;
    protected final float height;
    protected final float totalWidth;
    protected final float totalHeight;
    protected final SkinTextureOptions options;
    protected final SkinTextureData provider;

    public SkinTexturePos(float u, float v, float width, float height, SkinTextureData provider) {
        this(u, v, width, height, provider.width(), provider.height(), null, provider);
    }

    public SkinTexturePos(float u, float v, float width, float height, SkinTextureOptions options, SkinTextureData provider) {
        this(u, v, width, height, provider.width(), provider.height(), options, provider);
    }

    public SkinTexturePos(float u, float v, float width, float height, float totalWidth, float totalHeight) {
        this(u, v, width, height, totalWidth, totalHeight, null, null);
    }

    public SkinTexturePos(float u, float v, float width, float height, float totalWidth, float totalHeight, SkinTextureOptions options, SkinTextureData provider) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.options = options;
        this.provider = provider;
    }

    @Override
    public float u() {
        return u;
    }

    @Override
    public float v() {
        return v;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    @Override
    public float totalWidth() {
        return totalWidth;
    }

    @Override
    public float totalHeight() {
        return totalHeight;
    }

    @Override
    public SkinTextureOptions options() {
        return options;
    }

    @Nullable
    @Override
    public SkinTextureData provider() {
        return provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinTexturePos that)) return false;
        return Float.compare(that.u, u) == 0 && Float.compare(that.v, v) == 0 && Float.compare(that.width, width) == 0 && Float.compare(that.height, height) == 0 && Float.compare(that.totalWidth, totalWidth) == 0 && Float.compare(that.totalHeight, totalHeight) == 0 && that.options == options;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, width, height, totalWidth, totalHeight, options);
    }

    @Override
    public String toString() {
        return Objects.toString(this, "uv", new OpenVector2i(u, v), "size", new OpenVector2i(width, height), "total", new OpenVector2i(totalWidth, totalHeight), "options", options);
    }
}
