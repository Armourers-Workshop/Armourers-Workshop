package moe.plushie.armourers_workshop.api.core;

public interface IResultHandler<T> {

    void apply(T value, Exception exception);

    default void accept(T value) {
        apply(value, null);
    }

    default void abort(Exception exception) {
        apply(null, exception);
    }
}
