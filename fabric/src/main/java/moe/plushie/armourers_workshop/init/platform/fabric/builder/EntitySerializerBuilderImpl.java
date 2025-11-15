package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.compat.core.data.AbstractEntityDataSerializerBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;

public class EntitySerializerBuilderImpl<T> implements IEntitySerializerBuilder<T> {

    private final AbstractEntityDataSerializerBuilder<T> builder;

    public EntitySerializerBuilderImpl(IEntityDataSerializer<T> serializer) {
        this.builder = new AbstractEntityDataSerializerBuilder<>(serializer);
    }

    @Override
    public IRegistryHolder<EntityDataSerializer<T>> build(String name) {
        return Registries.ENTITY_DATA_SERIALIZERS.register(name, builder::build);
    }
}
