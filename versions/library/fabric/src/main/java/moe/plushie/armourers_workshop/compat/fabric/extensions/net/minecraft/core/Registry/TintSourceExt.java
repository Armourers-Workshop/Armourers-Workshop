package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class TintSourceExt {

    public static TypedProvider<IItemTintSourceType<?>> createItemTintSourceRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }

    public static TypedProvider<IBlockTintSourceType<?>> createBlockTintSourceRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
