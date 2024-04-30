package moe.plushie.armourers_workshop.compatibility.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;

@Available("[1.21, )")
public class AbstractEntityDataSerializer<T> implements IEntitySerializer<T> {

    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;

    protected AbstractEntityDataSerializer(EntityDataSerializer<T> serializer) {
        this.codec = serializer.codec();
    }

    public static <T>  Proxy<T> create(IEntitySerializer<T> serializer) {
        return new Proxy<>(serializer);
    }

    public static <T> AbstractEntityDataSerializer<T> wrap(EntityDataAccessor<T> accessor) {
        return wrap(accessor.serializer());
    }

    public static <T> AbstractEntityDataSerializer<T> wrap(EntityDataSerializer<T> serializer) {
        return new AbstractEntityDataSerializer<>(serializer);
    }

    @Override
    public T read(IFriendlyByteBuf buffer) {
        return codec.decode((RegistryFriendlyByteBuf) buffer.asByteBuf());
    }

    @Override
    public void write(IFriendlyByteBuf buffer, T value) {
        codec.encode((RegistryFriendlyByteBuf) buffer.asByteBuf(), value);
    }

    public static class Proxy<T> implements EntityDataSerializer<T> {

        private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
        private final IEntitySerializer<T> serializer;

        public Proxy(IEntitySerializer<T> serializer) {
            this.serializer = serializer;
            this.codec = StreamCodec.of(this::write, this::read);
        }

        public void write(RegistryFriendlyByteBuf buf, T object) {
            serializer.write(AbstractFriendlyByteBuf.wrap(buf), object);
        }

        public T read(RegistryFriendlyByteBuf buf) {
            return serializer.read(AbstractFriendlyByteBuf.wrap(buf));
        }

        @Override
        public T copy(T object) {
            return object;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, T> codec() {
            return codec;
        }

        @Override
        public String toString() {
            return serializer.toString();
        }
    }
}
