package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataContainer implements IAssociatedContainer {

    private final HashMap<Object, Object> values = new HashMap<>();

    public DataContainer() {
    }

    public static <T> Key<T> key(String name) {
        return new Key<>(name, null, null);
    }

    public static <T> Key<T> key(String name, T defaultValue) {
        return new Key<>(name, () -> defaultValue, null);
    }

    public static <T> Key<T> key(String name, Supplier<T> provider) {
        return new Key<>(name, null, it -> provider.get());
    }

    public static <T, S> Key<T> key(String name, Function<S, T> factory) {
        return new Key<>(name, null, factory);
    }

    public static <T, V> void set(T object, V value) {
        // noinspection unchecked
        set(object, (Key<V>) Builtin.DEFAULT, value);
    }

    public static <T, V> V get(T object) {
        // noinspection unchecked
        return get(object, (Key<V>) Builtin.DEFAULT);
    }

    public static <T, V> V getOrDefault(T object, V defaultValue) {
        // noinspection unchecked
        return getOrDefault(object, (Key<V>) Builtin.DEFAULT, defaultValue);
    }

    public static <T, V> V of(T object, Function<T, V> factory) {
        V value = get(object);
        if (value != null) {
            return value;
        }
        var newValue = factory.apply(object);
        set(object, newValue);
        return newValue;
    }

    public static <T, V> void set(T object, IAssociatedContainer.Key<V> key, V value) {
        var provider = (IAssociatedContainer) object;
        provider.setAssociatedObject(key, value);
    }

    public static <T, V> V get(T object, IAssociatedContainer.Key<V> key) {
        var provider = (IAssociatedContainer) object;
        return provider.getAssociatedObject(key);
    }

    public static <T, V> V getOrDefault(T object, IAssociatedContainer.Key<V> key, V defaultValue) {
        var value = get(object, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static <T, V> V of(T object, Key<V> key) {
        var provider = (IAssociatedContainer) object;
        var value = provider.getAssociatedObject(key);
        if (value != null) {
            return value;
        }
        // noinspection unchecked
        var factory = (Function<T, V>) key.factory;
        var newValue = factory.apply(object);
        provider.setAssociatedObject(key, newValue);
        return newValue;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        var value = getValue(key);
        if (value != null) {
            // noinspection unchecked
            return (T) value;
        }
        return key.defaultValue();
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, @Nullable T value) {
        setValue(key, value);
    }

    protected void setValue(IAssociatedContainer.Key<?> key, @Nullable Object value) {
        if (value != null) {
            values.put(key, value);
        } else {
            values.remove(key);
        }
    }

    @Nullable
    protected Object getValue(IAssociatedContainer.Key<?> key) {
        return values.get(key);
    }

    public static class Key<T> implements IAssociatedContainer.Key<T> {

        private static final AtomicInteger GENERATOR = new AtomicInteger();

        private final int id;
        private final String name;
        private final Function<?, T> factory;
        private final Supplier<T> defaultValue;

        private Key(String name, Supplier<T> defaultValue, Function<?, T> factory) {
            this.id = GENERATOR.getAndIncrement();
            this.name = name;
            this.factory = factory;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key<?> that)) return false;
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
        public T defaultValue() {
            if (defaultValue != null) {
                return defaultValue.get();
            }
            return null;
        }
    }

    public static class Builtin extends DataContainer {

        private static final DataContainer.Key<Object> DEFAULT = new DataContainer.Key<>("builtin", null, null);

        private Object builtin;

        @Nullable
        @Override
        protected Object getValue(IAssociatedContainer.Key<?> key) {
            if (key == DEFAULT) {
                return builtin;
            }
            return super.getValue(key);
        }

        @Override
        protected void setValue(IAssociatedContainer.Key<?> key, @Nullable Object value) {
            if (key == DEFAULT) {
                builtin = value;
            } else {
                super.setValue(key, value);
            }
        }
    }
}
