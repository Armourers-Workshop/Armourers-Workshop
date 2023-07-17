package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataStorage implements IAssociatedContainer {

    private final HashMap<IAssociatedContainerKey<?>, Supplier<Object>> values = new HashMap<>();

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
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
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        values.put(key, () -> value);
    }
}
