package moe.plushie.armourers_workshop.core.skin.part.item;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.action.ICanOverride;

import java.util.Collection;

public class OverrideItemPartType extends ItemPartType implements ICanOverride {

    private final Collection<String> overrides;

    public OverrideItemPartType(String... overrides) {
        super();
        this.overrides = Lists.newArrayList(overrides);
    }

    @Override
    public Collection<String> getItemOverrides() {
        return overrides;
    }
}
