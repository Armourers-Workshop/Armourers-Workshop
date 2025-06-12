package moe.plushie.armourers_workshop.api.skin.texture;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Collections;

public interface ISkinTextureProvider {

    String name();

    float width();

    float height();

    ByteBuf buffer();

    ISkinTextureAnimation animation();

    ISkinTextureProperties properties();

    default Collection<? extends ISkinTextureProvider> variants() {
        return Collections.emptyList();
    }
}
