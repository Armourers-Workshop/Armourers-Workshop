package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.builder.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;

public class EntitySerializerBuilderImpl<T> implements IEntitySerializerBuilder<T> {

    private final IEntitySerializer<T> serializer;

    public EntitySerializerBuilderImpl(IEntitySerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public EntityDataSerializer<T> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        EntityDataSerializer<T> dataSerializer = new EntityDataSerializer<T>() {
            @Override
            public void write(FriendlyByteBuf friendlyByteBuf, T object) {
                serializer.write(friendlyByteBuf, object);
            }

            @Override
            public T read(FriendlyByteBuf friendlyByteBuf) {
                return serializer.read(friendlyByteBuf);
            }

            @Override
            public T copy(T object) {
                return object;
            }
        };
        EntityDataSerializers.registerSerializer(dataSerializer);
        ModLog.debug("Registering '{}'", registryName);
        return dataSerializer;
    }
}
