package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

public interface ISkin {

    /**
     * Gets the type this skin.
     */
    <T extends ISkinType> T getType();

    List<? extends ISkinPart> getParts();
}
