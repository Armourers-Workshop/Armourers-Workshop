package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeKeyMapping;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[19, )")
@Extension
public class KeyMappingExt {

    public static TypedProvider<IKeyMapping> createKeyMappingRegistryFO(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough((registryName, value) -> {
            var keyMapping = AbstractForgeKeyMapping.unwrap(value);
            AbstractForgeClientEventsImpl.KEY_REGISTRY.listen(event -> event.register(keyMapping));
        });
    }
}
