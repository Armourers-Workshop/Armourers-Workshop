package moe.plushie.armourers_workshop.api.data;

import org.jetbrains.annotations.Nullable;

public interface IDataProperty<T> {

    void set(T value);

    @Nullable T get();

    default T getOrDefault(T defaultValue) {
        T value = get();
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
