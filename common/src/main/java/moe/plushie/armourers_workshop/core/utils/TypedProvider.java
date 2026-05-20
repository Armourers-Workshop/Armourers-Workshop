package moe.plushie.armourers_workshop.core.utils;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TypedProvider<T> {

    default <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> supplier) {
        throw new AssertionError();
    }

    @Nullable
    default OpenResourceKey getKey(T object) {
        return null;
    }

    @Nullable
    default T getValue(OpenResourceKey registryName) {
        return null;
    }

    static <T> TypedProvider<T> factory(Function<OpenResourceKey, T> factory) {
        return new TypedProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> provider) {
                T value = factory.apply(registryName);
                // noinspection unchecked
                return () -> (I) value;
            }
        };
    }

    static <T> TypedProvider<T> factory(BiFunction<OpenResourceKey, Supplier<? extends T>, Supplier<? extends T>> provider) {
        return new TypedProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> supplier) {
                Supplier<? extends T> value = provider.apply(registryName, () -> supplier.apply(registryName));
                // noinspection unchecked
                return (Supplier<I>) value;
            }
        };
    }

    static <T> TypedProvider<T> passthrough() {
        return new TypedProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> supplier) {
                I value = supplier.apply(registryName);
                return () -> value;
            }
        };
    }

    static <T> TypedProvider<T> passthrough(BiConsumer<OpenResourceKey, T> consumer) {
        return new TypedProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> supplier) {
                I value = supplier.apply(registryName);
                consumer.accept(registryName, value);
                return () -> value;
            }
        };
    }

    default <R> TypedProvider<R> map(Function<? super R, ? extends T> transform) {
        return new TypedProvider<>() {

            @Override
            public <I extends R> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> supplier) {
                var reference = new AtomicReference<I>();
                TypedProvider.this.register(registryName, it -> {
                    var value = supplier.apply(it);
                    reference.set(value); // create may be delayed.
                    return transform.apply(value);
                });
                return reference::get;
            }
        };
    }

    default <R> TypedProvider<R> flatMap(Function<? super R, ? extends T> transform) {
        return map(transform);
    }
}
