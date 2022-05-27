package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

public interface ISkin {

    /**
     * Gets the type this skin.
     */
    <T extends ISkinType> T getType();

    /**
     * Get the part type of this skin.
     */
    List<? extends ISkinPart> getParts();
}
