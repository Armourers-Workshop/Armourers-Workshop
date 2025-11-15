package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.22)")
@Extension
public class SpecialModelRendererExt {

    public static TypedProvider<ISpecialModelRendererType<?>> createSpecialModelRendererRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
