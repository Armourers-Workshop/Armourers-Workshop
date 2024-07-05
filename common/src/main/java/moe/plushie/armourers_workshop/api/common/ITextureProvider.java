package moe.plushie.armourers_workshop.api.common;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

public interface ITextureProvider {

    String getName();

    float getWidth();

    float getHeight();

    ByteBuffer getBuffer();

    ITextureAnimation getAnimation();

    ITextureProperties getProperties();

    default Collection<ITextureProvider> getVariants() {
        return Collections.emptyList();
    }
}
