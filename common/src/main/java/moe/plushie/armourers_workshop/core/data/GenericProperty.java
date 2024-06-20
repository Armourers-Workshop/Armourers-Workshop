package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericProperty;
import moe.plushie.armourers_workshop.core.network.CustomPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GenericProperty<S, T> implements IGenericProperty<S, T> {

    protected int ordinal;
    protected GenericProperties<S> owner;
    protected IEntitySerializer<T> serializer;

    protected Function<S, T> getter;
    protected BiConsumer<S, T> setter;


    public CustomPacket buildPacket(S source, T value) {
        return owner.encodePacket(this, value, source);
    }

    @Override
    public void set(S source, T value) {
        if (setter != null) {
            setter.accept(source, value);
        }
    }

    @Override
    public T get(S source) {
        if (getter != null) {
            return getter.apply(source);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(ordinal);
    }
}
