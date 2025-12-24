package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.26)")
@Extension
public class EntityDataSerializerExt {

    public static TypedProvider<EntityDataSerializer<?>> createEntityDataSerializerRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough((registryName, value) -> {
            // register to real item.
            EntityDataSerializers.registerSerializer(value);
        });
    }
}
