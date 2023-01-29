package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

public interface ISkinPart {

    /**
     * Gets the type this skin part.
     */
    ISkinPartType getType();

    List<? extends ISkinMarker> getMarkers();
}
