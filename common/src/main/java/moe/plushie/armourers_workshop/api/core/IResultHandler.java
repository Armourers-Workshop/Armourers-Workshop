package moe.plushie.armourers_workshop.api.core;

import java.util.function.BiConsumer;

public interface IResultHandler<T> {

    void apply(T value, Throwable exception);

    default void accept(T value) {
        apply(value, null);
    }

    default void abort(Throwable throwable) {
        apply(null, throwable);
    }
}
