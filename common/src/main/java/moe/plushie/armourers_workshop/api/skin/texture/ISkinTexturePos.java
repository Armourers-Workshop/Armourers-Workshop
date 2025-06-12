package moe.plushie.armourers_workshop.api.skin.texture;

import org.jetbrains.annotations.Nullable;

public interface ISkinTexturePos {

    float u();

    float v();

    float width();

    float height();

    float totalWidth();

    float totalHeight();

    @Nullable
    default ISkinTextureOptions options() {
        return null;
    }

    @Nullable
    default ISkinTextureProvider provider() {
        return null;
    }
}
