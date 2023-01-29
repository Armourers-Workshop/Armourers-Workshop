package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

public interface ISkin {

    /**
     * Gets the type this skin.
     */
    ISkinType getType();

    /**
     * Get the part type of this skin.
     */
    List<? extends ISkinPart> getParts();
}
