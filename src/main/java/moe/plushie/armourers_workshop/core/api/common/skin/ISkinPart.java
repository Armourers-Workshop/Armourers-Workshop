package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;

import java.util.List;

public interface ISkinPart {

    /**
     * Gets the type this skin part.
     */
    <T extends ISkinPartType> T getType();

    List<? extends ISkinMarker> getMarkers();
}
