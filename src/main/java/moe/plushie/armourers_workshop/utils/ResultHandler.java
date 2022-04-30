package moe.plushie.armourers_workshop.utils;

public interface ResultHandler<T> {

    void apply(T t, Exception e);

    default void accept(T t) {
        apply(t, null);
    }

    default void reject(Exception e) {
        apply(null, e);
    }
}
