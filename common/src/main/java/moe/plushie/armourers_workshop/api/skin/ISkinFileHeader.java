package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;

public interface ISkinFileHeader {

    int getVersion();

    ISkinType getType();

    ISkinProperties getProperties();
}
