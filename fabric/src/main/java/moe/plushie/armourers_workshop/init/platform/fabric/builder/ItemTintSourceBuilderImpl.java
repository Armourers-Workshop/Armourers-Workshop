package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractItemTintSourceBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class ItemTintSourceBuilderImpl<T extends ItemTintSource> implements IRegistryBuilder<ItemTintSourceType<T>> {

    private final AbstractItemTintSourceBuilder<T> builder;

    public ItemTintSourceBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractItemTintSourceBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<ItemTintSourceType<T>> build(String name) {
        return ClientRegistries.ITEM_TINT_SOURCE_TYPES.register(name, builder::build);
    }
}
