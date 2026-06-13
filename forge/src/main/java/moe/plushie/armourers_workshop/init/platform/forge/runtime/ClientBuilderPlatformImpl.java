package moe.plushie.armourers_workshop.init.platform.forge.runtime;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.init.platform.forge.builder.BlockTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ItemTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.forge.builder.SpecialModelRendererBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientBuilderFactory;

public class ClientBuilderPlatformImpl implements ClientBuilderFactory {

    @Override
    public <T extends ItemTintSource> IRegistryBuilder<ItemTintSourceType<T>> itemTintSource(IDataMapCodec<T> codec) {
        return new ItemTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends BlockTintSource> IRegistryBuilder<BlockTintSourceType<T>> blockTintSource(IDataMapCodec<T> codec) {
        return new BlockTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends SpecialModelRenderer<?>> IRegistryBuilder<SpecialModelRendererType<T>> specialModelRenderer(IDataMapCodec<T> codec) {
        return new SpecialModelRendererBuilderImpl<>(codec);
    }
}
