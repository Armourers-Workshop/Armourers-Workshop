package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

@Available("[21, 26)")
public interface AbstractResourceKey extends Supplier<ResourceLocation> {

    static OpenResourceKey wrap(ResourceLocation location) {
        return OpenResourceKey.create(location.getNamespace(), location.getPath());
    }

    String namespace();

    String path();

    @Override
    default ResourceLocation get() {
        return ResourceLocation.fromNamespaceAndPath(namespace(), path());
    }
}
