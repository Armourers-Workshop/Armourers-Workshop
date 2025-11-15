package moe.plushie.armourers_workshop.core.utils;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class FastMapper<K, V> {

    public static <K, V> FastMapper<K, V> builder(Consumer<Builder<K, V>> consumer) {
        return builder(null, null, consumer);
    }

    public static <K, V> FastMapper<K, V> builder(K defaultKey, V defaultValue, Consumer<Builder<K, V>> consumer) {
        var builder = new Builder<>(defaultKey, defaultValue);
        consumer.accept(builder);
        return new IdentityMapper<>(builder.defaultKey, builder.defaultValue, builder.keyToValue, builder.valueToKey);
    }

    public static <K extends Enum<K>, V extends Enum<V>> FastMapper<K, V> builder(K defaultKey, V defaultValue, Consumer<Builder<K, V>> consumer) {
        var builder = new Builder<>(defaultKey, defaultValue);
        consumer.accept(builder);
        return new EnumMapper<>(builder.defaultKey, builder.defaultValue, builder.keyToValue, builder.valueToKey);
    }

    public abstract K getKey(V value);

    public abstract V getValue(K key);

    public static class Builder<K, V> {

        private final IdentityHashMap<K, V> keyToValue = new IdentityHashMap<>();
        private final IdentityHashMap<V, K> valueToKey = new IdentityHashMap<>();

        private final K defaultKey;
        private final V defaultValue;

        public Builder(K defaultKey, V defaultValue) {
            this.defaultKey = defaultKey;
            this.defaultValue = defaultValue;
        }

        public void put(K key, V value) {
            keyToValue.put(key, value);
            valueToKey.put(value, key);
        }

        public void putKey(V value, K key) {
            valueToKey.put(value, key);
        }

        public void putValue(K key, V value) {
            keyToValue.put(key, value);
        }
    }

    private static class IdentityMapper<K, V> extends FastMapper<K, V> {

        private final IdentityHashMap<V, K> keys = new IdentityHashMap<>();
        private final IdentityHashMap<K, V> values = new IdentityHashMap<>();

        private final K defaultKey;
        private final V defaultValue;

        private IdentityMapper(K defaultKey, V defaultValue, Map<K, V> keyToValue, Map<V, K> valueToKey) {
            this.defaultKey = defaultKey;
            this.defaultValue = defaultValue;
            this.keys.putAll(valueToKey);
            this.values.putAll(keyToValue);
        }

        @Override
        public K getKey(V value) {
            return keys.getOrDefault(value, defaultKey);
        }

        @Override
        public V getValue(K key) {
            return values.getOrDefault(key, defaultValue);
        }
    }

    private static class EnumMapper<K extends Enum<K>, V extends Enum<V>> extends FastMapper<K, V> {

        private final K[] keys;
        private final V[] values;

        private EnumMapper(K defaultKey, V defaultValue, Map<K, V> keyToValue, Map<V, K> valueToKey) {
            var allKeys = defaultKey.getDeclaringClass().getEnumConstants();
            var allValues = defaultValue.getDeclaringClass().getEnumConstants();
            this.values = Arrays.copyOf(allValues, allKeys.length);
            for (var key : allKeys) {
                this.values[key.ordinal()] = keyToValue.getOrDefault(key, defaultValue);
            }
            this.keys = Arrays.copyOf(allKeys, allValues.length);
            for (var value : allValues) {
                this.keys[value.ordinal()] = valueToKey.getOrDefault(value, defaultKey);
            }
        }

        @Override
        public K getKey(V value) {
            return keys[value.ordinal()];
        }

        @Override
        public V getValue(K key) {
            return values[key.ordinal()];
        }
    }
}
