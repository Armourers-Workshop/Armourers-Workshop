package moe.plushie.armourers_workshop.api.common;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Collection;

public interface ITextureProvider {

    String getName();

    float getWidth();

    float getHeight();

    ByteBuffer getBuffer();

    ITextureAnimation getAnimation();

    ITextureProperties getProperties();

    @Nullable
    default Collection<ITextureProvider> getVariants() {
        return null;
    }
}
