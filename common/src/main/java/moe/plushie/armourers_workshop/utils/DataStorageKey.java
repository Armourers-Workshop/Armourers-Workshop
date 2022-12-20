package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.data.IExtraDateStorageKey;

import java.util.function.Supplier;

public class DataStorageKey<T> implements IExtraDateStorageKey<T> {

    private final String name;
    private final Class<T> type;
    private final Supplier<T> defaultValue;

    public DataStorageKey(String name, Class<T> type, Supplier<T> defaultValue) {
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
        if (!(o instanceof DataStorageKey)) return false;
        DataStorageKey<?> that = (DataStorageKey<?>) o;
        if (!name.equals(that.name)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
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
