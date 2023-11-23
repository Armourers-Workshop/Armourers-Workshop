package moe.plushie.armourers_workshop.api.skin;

import java.util.Collection;

public interface ISkinPart {

    /**
     * Gets the type this skin part.
     */
    ISkinPartType getType();

    Collection<? extends ISkinPart> getParts();

    Collection<? extends ISkinMarker> getMarkers();
}
