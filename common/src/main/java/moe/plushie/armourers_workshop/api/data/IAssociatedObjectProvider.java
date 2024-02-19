package moe.plushie.armourers_workshop.api.data;

import java.util.function.Function;

public interface IAssociatedObjectProvider {

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
