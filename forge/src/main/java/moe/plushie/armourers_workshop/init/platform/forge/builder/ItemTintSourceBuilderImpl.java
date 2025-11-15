package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IItemTintSourceBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractItemTintSourceBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class ItemTintSourceBuilderImpl<T extends IItemTintSource> implements IItemTintSourceBuilder<T> {

    private final AbstractItemTintSourceBuilder<T> builder;

    public ItemTintSourceBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractItemTintSourceBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<IItemTintSourceType<T>> build(String name) {
        return ClientRegistries.ITEM_TINT_SOURCE_TYPES.register(name, builder::build);
    }
}
