package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.data.IExtraDateStorage;
import moe.plushie.armourers_workshop.api.data.IExtraDateStorageKey;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataStorage implements IExtraDateStorage {

    private final HashMap<IExtraDateStorageKey<?>, Supplier<Object>> values = new HashMap<>();

    @Override
    public <T> T getExtraData(IExtraDateStorageKey<T> key) {
        Supplier<Object> value = values.get(key);
        if (value != null) {
            Object value1 = value.get();
            if (value1 != null) {
                return key.getType().cast(value1);
            }
            return null;
        }
        T value2 = key.getDefaultValue();
        values.put(key, () -> value2);
        return value2;
    }

    @Override
    public <T> void setExtraData(IExtraDateStorageKey<T> key, T value) {
        values.put(key, () -> value);
    }
}
