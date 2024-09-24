package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericProperties;
import moe.plushie.armourers_workshop.api.data.IGenericProperty;
import moe.plushie.armourers_workshop.api.data.IGenericValue;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.network.CustomPacket;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericProperties<S> implements IGenericProperties<S> {

    private final ArrayList<GenericProperty<S, ?>> properties = new ArrayList<>();
    private final BiFunction<S, IGenericValue<S, ?>, CustomPacket> factory;

    protected GenericProperties(BiFunction<S, IGenericValue<S, ?>, CustomPacket> factory) {
        this.factory = factory;
    }

    public static <S> GenericProperties<S> of(Class<S> clazz, BiFunction<S, IGenericValue<S, ?>, CustomPacket> factory) {
        return new GenericProperties<>(factory);
    }

    public <T> Builder<S, T> create(IEntitySerializer<T> serializer) {
        return new Builder<>(this, serializer);
    }

    @Override
    public IGenericValue<S, ?> read(IFriendlyByteBuf buf) {
        var ordinal = buf.readVarInt();
        var property = properties.get(ordinal);
        return decodePacket(property, buf);
    }

    protected <T> CustomPacket encodePacket(GenericProperty<S, T> property, T value, S source) {
        return factory.apply(source, new Holder<>(property, value));
    }

    protected <T> IGenericValue<S, T> decodePacket(GenericProperty<S, T> property, IFriendlyByteBuf buf) {
        var value = property.serializer.read(buf);
        return new Holder<>(property, value);
    }

    public static class Builder<S, T> {

        private final GenericProperties<S> owner;

        private final IEntitySerializer<T> serializer;

        private Function<S, T> getter;
        private BiConsumer<S, T> setter;

        private Builder(GenericProperties<S> owner, IEntitySerializer<T> serializer) {
            this.owner = owner;
            this.serializer = serializer;
        }

        public Builder<S, T> setter(BiConsumer<S, T> applier) {
            this.setter = applier;
            return this;
        }

        public Builder<S, T> getter(Function<S, T> supplier) {
            this.getter = supplier;
            return this;
        }

        public <P extends GenericProperty<S, T>> P build(Supplier<P> factory) {
            var property = factory.get();
            property.owner = owner;
            property.serializer = serializer;
            property.getter = getter;
            property.setter = setter;
            property.ordinal = owner.properties.size();
            owner.properties.add(property);
            return property;
        }

    }

    protected static class Holder<S, T> implements IGenericValue<S, T> {

        private final GenericProperty<S, T> property;
        private final T value;

        protected Holder(GenericProperty<S, T> property, T value) {
            this.property = property;
            this.value = value;
        }

        @Override
        public void apply(S source) {
            property.set(source, value);
        }

        @Override
        public void write(IFriendlyByteBuf buf) {
            buf.writeVarInt(property.ordinal);
            property.serializer.write(buf, value);
        }

        @Override
        public IGenericProperty<S, T> getProperty() {
            return property;
        }

        @Override
        public T getValue() {
            return value;
        }
    }
}

