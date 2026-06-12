package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class BlockTintSourceBuilderImpl<T extends BlockTintSource> implements IRegistryBuilder<BlockTintSourceType<T>> {

    private final AbstractBlockTintSourceBuilder<T> builder;

    public BlockTintSourceBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractBlockTintSourceBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<BlockTintSourceType<T>> build(String name) {
        return ClientRegistries.BLOCK_TINT_SOURCE_TYPES.register(name, builder::build);
    }
}
