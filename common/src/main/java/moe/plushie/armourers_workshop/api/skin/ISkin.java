package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPart;

import java.util.List;

public interface ISkin {

    /**
     * Gets the type this skin.
     */
    ISkinType type();

    /**
     * Get the part type of this skin.
     */
    List<? extends ISkinPart> parts();
}
