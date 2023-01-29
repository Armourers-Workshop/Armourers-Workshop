package moe.plushie.armourers_workshop.api.skin;

import java.util.List;

/**
 * @author RiskyKen
 */
public interface ISkinType extends ISkinRegistryEntry {

    int getId();

    List<? extends ISkinPartType> getParts();
}
