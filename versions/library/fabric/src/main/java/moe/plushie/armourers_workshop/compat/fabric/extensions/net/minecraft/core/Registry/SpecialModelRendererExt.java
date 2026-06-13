package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class SpecialModelRendererExt {

    public static TypedProvider<SpecialModelRendererType<?>> createSpecialModelRendererRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
