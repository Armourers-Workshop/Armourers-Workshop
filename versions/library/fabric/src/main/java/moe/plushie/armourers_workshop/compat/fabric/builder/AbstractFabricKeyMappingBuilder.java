package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.compat.builder.AbstractKeyMappingBuilder;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricKeyMapping;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractFabricKeyMappingBuilder<T extends IKeyMapping> extends AbstractKeyMappingBuilder<T> {

    public AbstractFabricKeyMappingBuilder(String key, IKeyCategory category) {
        super(key, category);
    }

    @Override
    protected IKeyMapping create(OpenResourceLocation registryName) {
        return new AbstractFabricKeyMapping(registryName, key, modifiers, category);
    }
}
