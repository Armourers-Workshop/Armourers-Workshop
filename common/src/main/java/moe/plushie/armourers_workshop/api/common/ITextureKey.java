package moe.plushie.armourers_workshop.api.common;

import org.jetbrains.annotations.Nullable;

public interface ITextureKey {

    boolean isMirror();

    float getU();

    float getV();

    float getWidth();

    float getHeight();

    float getTotalWidth();

    float getTotalHeight();

    @Nullable
    default ITextureOptions getOptions() {
        return null;
    }

    @Nullable
    default ITextureProvider getProvider() {
        return null;
    }
}
