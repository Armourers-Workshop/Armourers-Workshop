package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class DataContainerKey<T> implements IAssociatedContainerKey<T> {

    private static final AtomicInteger GENERATOR = new AtomicInteger();

    private final int id;
    private final String name;
    private final Class<T> type;
    private final Supplier<T> defaultValue;

    public DataContainerKey(String name, Class<T> type, Supplier<T> defaultValue) {
        this.id = GENERATOR.getAndIncrement();
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static <T> DataContainerKey<T> of(String name, Class<T> type) {
        return new DataContainerKey<>(name, type, null);
    }

    public static <T> DataContainerKey<T> of(String name, Class<T> type, Supplier<T> provider) {
        return new DataContainerKey<>(name, type, provider);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataContainerKey<?> that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T defaultValue() {
        if (defaultValue != null) {
            return defaultValue.get();
        }
        return null;
    }
}
