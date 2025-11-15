package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class BlockTintSourceBuilderImpl<T extends IBlockTintSource> implements IBlockTintSourceBuilder<T> {

    private final AbstractBlockTintSourceBuilder<T> builder;

    public BlockTintSourceBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractBlockTintSourceBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<IBlockTintSourceType<T>> build(String name) {
        return ClientRegistries.BLOCK_TINT_SOURCE_TYPES.register(name, builder::build);
    }
}
