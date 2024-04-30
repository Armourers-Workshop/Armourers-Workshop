package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IItemTagBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;

public class ItemTagBuilderImpl<T extends IItemTag> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return AbstractFabricRegistries.ITEM_TAGS.register(name, null);
    }
}
