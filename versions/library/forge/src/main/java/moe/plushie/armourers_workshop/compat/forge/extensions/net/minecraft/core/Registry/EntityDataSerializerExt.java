package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.network.syncher.EntityDataSerializer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class EntityDataSerializerExt {

    public static TypedProvider<EntityDataSerializer<?>> createEntityDataSerializerRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.ENTITY_DATA_SERIALIZER);
    }
}
