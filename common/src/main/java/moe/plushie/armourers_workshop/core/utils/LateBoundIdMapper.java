package moe.plushie.armourers_workshop.core.utils;

import com.mojang.serialization.DataResult;
import moe.plushie.armourers_workshop.api.core.IDataCodec;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class LateBoundIdMapper<I, V> {

    private final Map<I, V> keyToValue = new HashMap<>();
    private final Map<V, I> valueToKey = new IdentityHashMap<>();

    public LateBoundIdMapper<I, V> put(I key, V value) {
        this.keyToValue.put(key, value);
        this.valueToKey.put(value, key);
        return this;
    }

    public IDataCodec<V> codec(IDataCodec<I> keyCodec) {
        return keyCodec.flatXmap(key -> {
            var value = getValue(key);
            if (value == null) {
                return DataResult.error(() -> "Unknown element id: " + key);
            }
            return DataResult.success(value);
        }, value -> {
            var key = getKey(value);
            if (key == null) {
                return DataResult.error(() -> "Element with unknown id: " + value);
            }
            return DataResult.success(key);
        });
    }

    public I getKey(V value) {
        return valueToKey.get(value);
    }

    public V getValue(I key) {
        return keyToValue.get(key);
    }
}
