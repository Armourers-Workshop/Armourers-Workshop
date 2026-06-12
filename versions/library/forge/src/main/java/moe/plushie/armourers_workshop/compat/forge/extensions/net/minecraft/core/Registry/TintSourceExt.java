package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class TintSourceExt {

    public static TypedProvider<ItemTintSourceType<?>> createItemTintSourceRegistryFO(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }

    public static TypedProvider<BlockTintSourceType<?>> createBlockTintSourceRegistryFO(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
