package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;

import java.util.HashMap;
import java.util.function.Function;

public class DataContainer implements IAssociatedContainerProvider {

    private final HashMap<Object, Object> values = new HashMap<>();

    public DataContainer() {
    }

    public static <T, V> void set(T object, V value) {
        // noinspection unchecked
        set(object, (IAssociatedContainerKey<V>) Builtin.DEFAULT, value);
    }

    public static <T, V> V get(T object) {
        // noinspection unchecked
        return get(object, (IAssociatedContainerKey<V>) Builtin.DEFAULT);
    }

    public static <T, V> V getOrDefault(T object, V defaultValue) {
        // noinspection unchecked
        return getOrDefault(object, (IAssociatedContainerKey<V>) Builtin.DEFAULT, defaultValue);
    }

    public static <T, V> V of(T object, Function<T, V> supplier) {
        // noinspection unchecked
        return of(object, (IAssociatedContainerKey<V>) Builtin.DEFAULT, supplier);
    }

    public static <T, V> void set(T object, IAssociatedContainerKey<V> key, V value) {
        var provider = (IAssociatedContainerProvider) object;
        provider.setAssociatedObject(key, value);
    }

    public static <T, V> V get(T object, IAssociatedContainerKey<V> key) {
        var provider = (IAssociatedContainerProvider) object;
        return provider.getAssociatedObject(key);
    }

    public static <T, V> V getOrDefault(T object, IAssociatedContainerKey<V> key, V defaultValue) {
        var value = get(object, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static <T, V> V of(T object, IAssociatedContainerKey<V> key, Function<T, V> supplier) {
        var provider = (IAssociatedContainerProvider) object;
        var value = provider.getAssociatedObject(key);
        if (value != null) {
            return value;
        }
        var newValue = supplier.apply(object);
        provider.setAssociatedObject(key, newValue);
        return newValue;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        var value = getValue(key);
        if (value != null) {
            return key.type().cast(value);
        }
        return key.defaultValue();
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        setValue(key, value);
    }

    protected void setValue(IAssociatedContainerKey<?> key, Object value) {
        values.put(key, value);
    }

    protected Object getValue(IAssociatedContainerKey<?> key) {
        return values.get(key);
    }

    public static class Builtin extends DataContainer {

        private static final DataContainerKey<Object> DEFAULT = new DataContainerKey<>("builtin", Object.class, null);

        private Object builtin;

        @Override
        protected Object getValue(IAssociatedContainerKey<?> key) {
            if (key == DEFAULT) {
                return builtin;
            } else {
                return super.getValue(key);
            }
        }

        @Override
        protected void setValue(IAssociatedContainerKey<?> key, Object value) {
            if (key == DEFAULT) {
                builtin = value;
            } else {
                super.setValue(key, value);
            }
        }
    }
}
