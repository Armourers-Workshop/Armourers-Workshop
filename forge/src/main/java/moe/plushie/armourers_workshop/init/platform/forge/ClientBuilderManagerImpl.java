package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemTintSourceBuilder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;
import moe.plushie.armourers_workshop.init.platform.forge.builder.BlockTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ItemTintSourceBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.forge.builder.SpecialModelRendererBuilderImpl;

@SuppressWarnings("unused")
public class ClientBuilderManagerImpl extends ClientBuilderManager {

    @Override
    public <T extends IItemTintSource> IItemTintSourceBuilder<T> createItemTintSourceBuilder(IDataMapCodec<T> codec) {
        return new ItemTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends IBlockTintSource> IBlockTintSourceBuilder<T> createBlockTintSourceBuilder(IDataMapCodec<T> codec) {
        return new BlockTintSourceBuilderImpl<>(codec);
    }

    @Override
    public <T extends ISpecialModelRenderer<?>> ISpecialModelRendererBuilder<T> createSpecialModelRendererBuilder(IDataMapCodec<T> codec) {
        return new SpecialModelRendererBuilderImpl<>(codec);
    }
}
