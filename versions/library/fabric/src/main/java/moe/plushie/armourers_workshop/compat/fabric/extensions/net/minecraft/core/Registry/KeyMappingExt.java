package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricKeyMapping;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class KeyMappingExt {

    public static TypedProvider<IKeyMapping> createKeyMappingRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough((registryName, value) -> {
            var keyMapping = AbstractFabricKeyMapping.unwrap(value);
            KeyBindingHelper.registerKeyBinding(keyMapping);
        });
    }
}
