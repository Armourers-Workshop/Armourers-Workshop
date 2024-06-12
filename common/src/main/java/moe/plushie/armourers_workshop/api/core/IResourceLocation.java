package moe.plushie.armourers_workshop.api.core;

import net.minecraft.resources.ResourceLocation;

public interface IResourceLocation {

    String getNamespace();

    String getPath();

    default ResourceLocation toLocation() {
        return ResourceLocation.create(getNamespace(), getPath());
    }

    default String toLanguageKey() {
        return getNamespace() + "." + getPath();
    }

    default String toLanguageKey(String prefix) {
        return prefix + "." + toLanguageKey();
    }

    default String toLanguageKey(String prefix, String suffix) {
        return prefix + "." + toLanguageKey() + "." + suffix;
    }
}
