package moe.plushie.armourers_workshop.core.data.cache;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class CacheQueue<K, V> {

    private long nextCheckTime;

    private final int expiredTime;
    private final Consumer<V> releaseHandler;

    private final ArrayList<Pair<K, V>> queuing = new ArrayList<>();
    private final HashMap<K, Entry<V>> values = new HashMap<>();

    public CacheQueue(int expiredTime) {
        this(expiredTime, null);
    }

    public CacheQueue(int expiredTime, Consumer<V> releaseHandler) {
        this.expiredTime = expiredTime;
        this.releaseHandler = releaseHandler;
    }

    public void clearAll() {
        if (releaseHandler != null) {
            for (var entry : values.values()) {
                releaseHandler.accept(entry.value);
            }
        }
        values.clear();
    }

    public void put(K key, V value) {
        var entry = new Entry<>(value);
        entry.expiredTime = System.currentTimeMillis() + expiredTime;
        values.put(key, entry);
    }

    public V get(K key) {
        var time = System.currentTimeMillis();
        var entry = values.get(key);
        if (entry != null) {
            entry.expiredTime = time + expiredTime;
            drain(time);
            return entry.value;
        }
        return null;
    }

    private void drain(long time) {
        if (nextCheckTime > time) {
            return;
        }
        nextCheckTime = time + expiredTime;
        for (var entry : values.entrySet()) {
            var value = entry.getValue();
            if (value.expiredTime > time) {
                continue;
            }
            queuing.add(Pair.of(entry.getKey(), value.value));
        }
        for (var entry : queuing) {
            values.remove(entry.getKey());
            if (releaseHandler != null) {
                releaseHandler.accept(entry.getValue());
            }
        }
        queuing.clear();
    }

    public static class Entry< V> {

        private long expiredTime;
        private final V value;

        public Entry(V value) {
            this.value = value;
        }
    }
}
