package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

public interface ISkinPart {

    /**
     * Gets the type this skin part.
     */
    <T extends ISkinPartType> T getType();

    List<? extends ISkinMarker> getMarkers();
}
