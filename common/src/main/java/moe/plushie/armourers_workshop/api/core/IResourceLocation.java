package moe.plushie.armourers_workshop.api.core;

import net.minecraft.resources.ResourceLocation;

public interface IResourceLocation {

    String namespace();

    String path();

    IResourceLocation withNamespace(String namespace);

    IResourceLocation withPath(String path);

    default ResourceLocation toLocation() {
        return ResourceLocation.create(namespace(), path());
    }

    default String toLanguageKey() {
        return namespace() + "." + path();
    }

    default String toLanguageKey(String prefix) {
        return prefix + "." + toLanguageKey();
    }

    default String toLanguageKey(String prefix, String suffix) {
        return prefix + "." + toLanguageKey() + "." + suffix;
    }
}
