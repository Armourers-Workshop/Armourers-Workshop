package moe.plushie.armourers_workshop.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TypedEventRegistry<E> {

    static <E> TypedEventRegistry<E> factory(Supplier<E> factory) {
        return factory(null, factory);
    }

    static <E> TypedEventRegistry<E> factory(Class<E> type, Supplier<E> factory) {
        return new TypedEventRegistry<E>() {

            @Override
            public void observer(Consumer<E> handler) {
                handler.accept(factory.get());
            }

            @Override
            public <T> void observer(Consumer<T> handler, Function<E, T> transform) {
                handler.accept(transform.apply(factory.get()));
            }
        };
    }

    void observer(Consumer<E> handler);

    <T> void observer(Consumer<T> handler, Function<E, T> transform);


    default <O> TypedEventRegistry<O> map(Function<E, O> transformer) {
        TypedEventRegistry<E> registry = this;
        return new TypedEventRegistry<O>() {
            @Override
            public void observer(Consumer<O> handler) {
                registry.observer(event -> {
                    handler.accept(transformer.apply(event));
                });
            }

            @Override
            public <T> void observer(Consumer<T> handler, Function<O, T> transform) {
                registry.observer(handler, event -> transform.apply(transformer.apply(event)));
            }
        };
    }

    default <O> TypedEventRegistry<O> flatMap(Function<E, O> transformer) {
        TypedEventRegistry<E> registry = this;
        return new TypedEventRegistry<O>() {
            @Override
            public void observer(Consumer<O> handler) {
                registry.observer(event -> {
                    O value = transformer.apply(event);
                    if (value != null) {
                        handler.accept(value);
                    }
                });
            }

            @Override
            public <T> void observer(Consumer<T> handler, Function<O, T> transform) {
                registry.observer(event -> {
                    if (event != null) {
                        handler.accept(event);
                    }
                }, event -> {
                    O value = transformer.apply(event);
                    if (value != null) {
                        return transform.apply(value);
                    }
                    return null;
                });
            }
        };
    }
}
