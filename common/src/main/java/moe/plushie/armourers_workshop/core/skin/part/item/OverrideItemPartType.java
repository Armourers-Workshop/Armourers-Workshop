package moe.plushie.armourers_workshop.core.skin.part.item;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanOverride;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.Collection;

public class OverrideItemPartType extends ItemPartType implements ICanOverride {

    private final Collection<String> overrides;

    public OverrideItemPartType(String... overrides) {
        super();
        this.overrides = Collections.newList(overrides);
    }

    @Override
    public Collection<String> itemOverrides() {
        return overrides;
    }
}
