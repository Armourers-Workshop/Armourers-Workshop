package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

@Available("[1.16, )")
public interface AbstractResourceLocation extends Supplier<ResourceLocation> {

    static OpenResourceLocation wrap(ResourceLocation location) {
        return OpenResourceLocation.create(location.getNamespace(), location.getPath());
    }

    String namespace();

    String path();

    @Override
    default ResourceLocation get() {
        return ResourceLocation.create(namespace(), path());
    }
}
