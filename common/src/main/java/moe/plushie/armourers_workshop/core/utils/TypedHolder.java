package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;

import java.util.function.Supplier;

public class TypedHolder<T> implements IRegistryHolder<T> {

    private final Supplier<T> value;
    private final OpenResourceKey registryName;

    public TypedHolder(OpenResourceKey registryName, Supplier<T> value) {
        this.value = value;
        this.registryName = registryName;
    }

    public static <T> TypedHolder<T> of(OpenResourceKey registryName, Supplier<T> value) {
        return new TypedHolder<>(registryName, value);
    }

    public static <T extends S, S> TypedHolder<T> cast(OpenResourceKey registryName, Supplier<S> value) {
        return new TypedHolder<>(registryName, Objects.unsafeCast(value));
    }

    public static <T> TypedHolder<T> ofValue(OpenResourceKey registryName, T value) {
        return of(registryName, () -> value);
    }

    public static <T extends S, S> TypedHolder<T> castValue(OpenResourceKey registryName, S value) {
        return cast(registryName, () -> value);
    }

    @Override
    public OpenResourceKey registryName() {
        return registryName;
    }

    @Override
    public T get() {
        return value.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IRegistryHolder<?> that)) return false;
        return registryName.equals(that.registryName());
    }

    @Override
    public int hashCode() {
        return registryName.hashCode();
    }
}
