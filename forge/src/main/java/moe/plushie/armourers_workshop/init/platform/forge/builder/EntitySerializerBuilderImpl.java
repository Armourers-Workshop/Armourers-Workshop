package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class EntitySerializerBuilderImpl<T> implements IEntitySerializerBuilder<T> {

    private final IEntitySerializer<T> serializer;

    public EntitySerializerBuilderImpl(IEntitySerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public EntityDataSerializer<T> build(String name) {
        Proxy<T> proxy = new Proxy<>(serializer);
        Registry.registerEntityDataSerializerFO(name, () -> proxy);
        return proxy;
    }

    public static class Proxy<T> implements EntityDataSerializer<T> {

        private final IEntitySerializer<T> serializer;

        public Proxy(IEntitySerializer<T> serializer) {
            this.serializer = serializer;
        }

        @Override
        public void write(FriendlyByteBuf buf, T object) {
            serializer.write(buf, object);
        }

        @Override
        public T read(FriendlyByteBuf buf) {
            return serializer.read(buf);
        }

        @Override
        public T copy(T object) {
            return object;
        }

        @Override
        public String toString() {
            return serializer.toString();
        }
    }
}
