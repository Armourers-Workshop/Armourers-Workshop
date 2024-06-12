package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class DataStorageKey<T> implements IAssociatedContainerKey<T> {

    private static final AtomicInteger GENERATOR = new AtomicInteger();

    private final int id;
    private final String name;
    private final Class<T> type;
    private final Supplier<T> defaultValue;

    public DataStorageKey(String name, Class<T> type, Supplier<T> defaultValue) {
        this.id = GENERATOR.getAndIncrement();
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static <T> DataStorageKey<T> of(String name, Class<T> type) {
        return new DataStorageKey<>(name, type, null);
    }

    public static <T> DataStorageKey<T> of(String name, Class<T> type, Supplier<T> provider) {
        return new DataStorageKey<>(name, type, provider);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStorageKey<?> that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public T getDefaultValue() {
        if (defaultValue != null) {
            return defaultValue.get();
        }
        return null;
    }
}
