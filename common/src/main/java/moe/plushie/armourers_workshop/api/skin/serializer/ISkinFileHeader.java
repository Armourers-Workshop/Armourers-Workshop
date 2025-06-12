package moe.plushie.armourers_workshop.api.skin.serializer;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;

public interface ISkinFileHeader {

    int version();

    int lastModified();

    ISkinType type();

    ISkinProperties properties();
}
