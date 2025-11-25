package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IKeyCategoryBuilder;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeKeyCategoryBuilder;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import moe.plushie.armourers_workshop.init.ModConstants;

public class KeyCategoryBuilderImpl<T extends IKeyCategory> implements IKeyCategoryBuilder<T> {

    public KeyCategoryBuilderImpl() {
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        var builder = new AbstractForgeKeyCategoryBuilder<T>(name);
        var registryName = ModConstants.key(name);
        return TypedHolder.ofValue(registryName, builder.build(registryName));
    }
}
