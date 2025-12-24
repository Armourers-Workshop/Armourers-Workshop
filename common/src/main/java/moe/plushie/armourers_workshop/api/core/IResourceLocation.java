package moe.plushie.armourers_workshop.api.core;

import moe.plushie.armourers_workshop.compat.core.AbstractResourceLocation;

public interface IResourceLocation extends AbstractResourceLocation {

    String namespace();

    String path();

    IResourceLocation withNamespace(String namespace);

    IResourceLocation withPath(String path);

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
