package moe.plushie.armourers_workshop.api.skin.texture;

import java.util.Collection;
import java.util.Collections;

public interface ISkinTextureData {

    String name();

    float width();

    float height();

    ISkinTextureAnimation animation();

    ISkinTextureProperties properties();

    byte[] bytes();

    default Collection<? extends ISkinTextureData> variants() {
        return Collections.emptyList();
    }
}
