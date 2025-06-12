package moe.plushie.armourers_workshop.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class EnumMapper<K extends Enum<K>, V extends Enum<V>> {

    private final V[] idToValues;
    private final K[] idToKeys;

    private EnumMapper(K[] idToKeys, V[] idToValues) {
        this.idToKeys = idToKeys;
        this.idToValues = idToValues;
    }

    public static <K extends Enum<K>, V extends Enum<V>> EnumMapper<K, V> create(K defaultKey, V defaultValue, Consumer<Builder<K, V>> consumer) {
        var builder = new Builder<>(defaultKey, defaultValue);
        consumer.accept(builder);
        return builder.build();
    }

    public K getKey(V obj) {
        return idToKeys[obj.ordinal()];
    }

    public V getValue(K obj) {
        return idToValues[obj.ordinal()];
    }

    public static class Builder<K extends Enum<K>, V extends Enum<V>> {

        private final HashMap<K, V> keyMap = new HashMap<>();
        private final HashMap<V, K> valueMap = new HashMap<>();

        private final K[] allKeys;
        private final V[] allValues;

        private final K defaultKey;
        private final V defaultValue;

        public Builder(K defaultKey, V defaultValue) {
            this.allKeys = defaultKey.getDeclaringClass().getEnumConstants();
            this.allValues = defaultValue.getDeclaringClass().getEnumConstants();
            this.defaultKey = defaultKey;
            this.defaultValue = defaultValue;
        }

        public void put(K key, V value) {
            keyMap.put(key, value);
            valueMap.put(value, key);
        }

        private EnumMapper<K, V> build() {
            var idToValues = Arrays.copyOf(allValues, allKeys.length);
            for (var key : allKeys) {
                idToValues[key.ordinal()] = keyMap.getOrDefault(key, defaultValue);
            }
            var idToKeys = Arrays.copyOf(allKeys, allValues.length);
            for (var value : allValues) {
                idToKeys[value.ordinal()] = valueMap.getOrDefault(value, defaultKey);
            }
            return new EnumMapper<>(idToKeys, idToValues);
        }
    }
}
