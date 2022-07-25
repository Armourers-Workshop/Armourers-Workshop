package moe.plushie.armourers_workshop.api;

public interface IResultHandler<T> {

    void apply(T t, Exception e);

    default void accept(T t) {
        apply(t, null);
    }

    default void reject(Exception e) {
        apply(null, e);
    }
}
