package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class TintSourceExt {

    public static TypedProvider<ItemTintSourceType<?>> createItemTintSourceRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }

    public static TypedProvider<BlockTintSourceType<?>> createBlockTintSourceRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
