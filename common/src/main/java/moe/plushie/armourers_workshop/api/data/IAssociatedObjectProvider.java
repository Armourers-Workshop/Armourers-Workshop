package moe.plushie.armourers_workshop.api.data;

import java.util.function.Function;

public interface IAssociatedObjectProvider {

    static <T, V> void set(T object, V value) {
        IAssociatedObjectProvider provider = (IAssociatedObjectProvider) object;
        provider.setAssociatedObject(value);
    }

    static <T, V> V get(T object, V defaultValue) {
        IAssociatedObjectProvider provider = (IAssociatedObjectProvider) object;
        V value = provider.getAssociatedObject();
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    static <T, V> V of(T object, Function<T, V> supplier) {
        IAssociatedObjectProvider provider = (IAssociatedObjectProvider) object;
        V value = provider.getAssociatedObject();
        if (value == null) {
            value = supplier.apply(object);
            provider.setAssociatedObject(value);
        }
        return value;
    }

    <T> T getAssociatedObject();

    <T> void setAssociatedObject(T data);
}
