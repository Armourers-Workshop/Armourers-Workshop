package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ITagKeyBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.item.Item;

public class ItemTagBuilderImpl<T extends Item> implements ITagKeyBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IRegistryHolder<ITagKey<T>> build(String name) {
        return Registries.ITEM_TAGS.register(name, null);
    }
}
