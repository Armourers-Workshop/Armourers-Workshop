package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractEntityDataSerializer;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;

public class EntitySerializerBuilderImpl<T> implements IEntitySerializerBuilder<T> {

    private final IEntitySerializer<T> serializer;

    public EntitySerializerBuilderImpl(IEntitySerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public EntityDataSerializer<T> build(String name) {
        EntityDataSerializer<T> proxy = AbstractEntityDataSerializer.create(serializer);
        AbstractForgeRegistries.ENTITY_DATA_SERIALIZER.register(name, () -> proxy);
        return proxy;
    }
}
