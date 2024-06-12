package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.resources.ResourceLocation;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.resources.ResourceLocation;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class Constructor {

    public static  ResourceLocation create(@ThisClass Class<?> clazz, String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
