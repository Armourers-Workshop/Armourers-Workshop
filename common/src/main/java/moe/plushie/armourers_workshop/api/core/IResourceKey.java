package moe.plushie.armourers_workshop.api.core;

import moe.plushie.armourers_workshop.compat.core.AbstractResourceKey;

public interface IResourceKey extends AbstractResourceKey {

    String namespace();

    String path();

    IResourceKey withNamespace(String namespace);

    IResourceKey withPath(String path);

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
