package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;

public interface ClientBuilderFactory {

    <T extends ItemTintSource> IRegistryBuilder<ItemTintSourceType<T>> itemTintSource(IDataMapCodec<T> codec);

    <T extends BlockTintSource> IRegistryBuilder<BlockTintSourceType<T>> blockTintSource(IDataMapCodec<T> codec);

    <T extends ISpecialModelRenderer<?>> ISpecialModelRendererBuilder<T> specialModelRenderer(IDataMapCodec<T> codec);
}
