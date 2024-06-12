package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IItemTagBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;

public class ItemTagBuilderImpl<T extends IItemTag> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return AbstractForgeRegistries.ITEM_TAGS.register(name, null);
    }
}
