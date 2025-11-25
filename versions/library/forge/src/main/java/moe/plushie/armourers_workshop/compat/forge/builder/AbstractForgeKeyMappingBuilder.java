package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.compat.builder.AbstractKeyMappingBuilder;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeKeyMapping;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractForgeKeyMappingBuilder<T extends IKeyMapping> extends AbstractKeyMappingBuilder<T> {

    public AbstractForgeKeyMappingBuilder(String key, IKeyCategory category) {
        super(key, category);
    }

    @Override
    protected IKeyMapping create(OpenResourceLocation registryName) {
        return new AbstractForgeKeyMapping(registryName, key, modifiers, category);
    }
}
