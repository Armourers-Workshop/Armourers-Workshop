package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

public interface ClientRegistryFactory {

    TypedProvider<IKeyMapping> keyMapping();

    TypedProvider<ItemTintSourceType<?>> itemTintSource();

    TypedProvider<BlockTintSourceType<?>> blockTintSource();

    TypedProvider<SpecialModelRendererType<?>> specialModelRendererType();
}
