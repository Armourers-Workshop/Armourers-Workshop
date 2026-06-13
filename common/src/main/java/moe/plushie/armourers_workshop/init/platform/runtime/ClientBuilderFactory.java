package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;

public interface ClientBuilderFactory {

    <T extends ItemTintSource> IRegistryBuilder<ItemTintSourceType<T>> itemTintSource(IDataMapCodec<T> codec);

    <T extends BlockTintSource> IRegistryBuilder<BlockTintSourceType<T>> blockTintSource(IDataMapCodec<T> codec);

    <T extends SpecialModelRenderer<?>> IRegistryBuilder<SpecialModelRendererType<T>> specialModelRenderer(IDataMapCodec<T> codec);
}
