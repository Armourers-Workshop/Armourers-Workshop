package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.api.ISkinType;

import java.util.List;

public interface ISkin {

    /**
     * Gets the type this skin.
     */
    <T extends ISkinType> T getType();

    List<? extends ISkinPart> getParts();
}
