package moe.plushie.armourers_workshop.init.platform.fabric.runtime;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientBuilderFactory;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.SpecialModelRendererBuilderImpl;

public class ClientBuilderFactoryImpl implements ClientBuilderFactory {

    @Override
    public <T extends ItemTintSource> IRegistryBuilder<ItemTintSourceType<T>> itemTintSource(IDataMapCodec<T> codec) {
        return new ItemTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends BlockTintSource> IRegistryBuilder<BlockTintSourceType<T>> blockTintSource(IDataMapCodec<T> codec) {
        return new BlockTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends ISpecialModelRenderer<?>> ISpecialModelRendererBuilder<T> specialModelRenderer(IDataMapCodec<T> codec) {
        return new SpecialModelRendererBuilderImpl<>(codec);
    }
}
