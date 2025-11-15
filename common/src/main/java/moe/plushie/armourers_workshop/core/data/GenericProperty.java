package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.core.network.CustomPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GenericProperty<S, T> {

    protected int ordinal;
    protected GenericProperties<S> owner;
    protected IEntityDataSerializer<T> serializer;

    protected Function<S, T> getter;
    protected BiConsumer<S, T> setter;

    public CustomPacket buildPacket(S source, T value) {
        return owner.encodePacket(this, value, source);
    }

    public void set(S source, T value) {
        if (setter != null) {
            setter.accept(source, value);
        }
    }

    public T get(S source) {
        if (getter != null) {
            return getter.apply(source);
        }
        return null;
    }

    public T getOrDefault(S source, T defaultValue) {
        T value = get(source);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return String.valueOf(ordinal);
    }
}
