package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataAccessor<S, T> {

    protected final IEntitySerializer<T> serializer;

    protected final Function<S, T> getter;
    protected final BiConsumer<S, T> setter;

    public DataAccessor(IEntitySerializer<T> serializer, Function<S, T> getter, BiConsumer<S, T> setter) {
        this.serializer = serializer;
        this.setter = setter;
        this.getter = getter;
    }

    public static <S, T> DataAccessor<S, Object> erased(IEntitySerializer<T> serializer, Function<S, T> getter, BiConsumer<S, T> setter) {
        DataAccessor<S, T> accessor = new DataAccessor<>(serializer, getter, setter);
        return ObjectUtils.unsafeCast(accessor);
    }


    public T read(FriendlyByteBuf buf) {
        return serializer.read(buf);
    }

    public void write(FriendlyByteBuf buf, T value) {
        serializer.write(buf, value);
    }

    public void set(S obj, T value) {
        if (setter != null) {
            setter.accept(obj, value);
        }
    }

    public T get(S obj) {
        if (getter != null) {
            return getter.apply(obj);
        }
        return null;
    }

    public T getOrDefault(S obj, T defaultValue) {
        T value = get(obj);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public interface Provider<S> {

        DataAccessor<S, Object> getAccessor();

        default <T> void set(S obj, T value) {
            getAccessor().set(obj, value);
        }

        default <T> T get(S obj) {
            Object value = getAccessor().get(obj);
            if (value != null) {
                return ObjectUtils.unsafeCast(value);
            }
            return null;
        }

        default <T> T getOrDefault(S obj, T defaultValue) {
            T value = get(obj);
            if (value != null) {
                return value;
            }
            return defaultValue;
        }
    }
}
