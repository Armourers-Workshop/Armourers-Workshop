package moe.plushie.armourers_workshop.api.common;

public interface IResultHandler<T> {

    void apply(T value, Exception exception);

    default void accept(T value) {
        apply(value, null);
    }

    default void reject(Exception exception) {
        apply(null, exception);
    }
}
