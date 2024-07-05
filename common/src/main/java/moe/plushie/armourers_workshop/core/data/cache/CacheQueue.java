package moe.plushie.armourers_workshop.core.data.cache;

import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CacheQueue<K, V> extends AutoreleasePool {

    private long nextCheckTime;

    private final long expiredTime;
    private final Consumer<V> releaseHandler;

    private final ArrayList<Pair<K, V>> queuing = new ArrayList<>();
    private final HashMap<K, Entry<V>> values = new HashMap<>();

    public CacheQueue(Duration expiredTime) {
        this(expiredTime, null);
    }

    public CacheQueue(Duration expiredTime, Consumer<V> releaseHandler) {
        this.expiredTime = expiredTime.toMillis();
        this.releaseHandler = releaseHandler;
    }


    @Override
    protected void beginCapturing() {

    }

    @Override
    protected void endCapturing() {
        drain(System.currentTimeMillis());
    }

    public void clearAll() {
        if (releaseHandler != null) {
            for (var entry : values.values()) {
                releaseHandler.accept(entry.value);
            }
        }
        values.clear();
    }

    public V remove(K key) {
        var entry = values.remove(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    public V put(K key, V value) {
        var entry = new Entry<>(value);
        entry.expiredTime = System.currentTimeMillis() + expiredTime;
        var oldEntry = values.put(key, entry);
        if (oldEntry != null) {
            return oldEntry.value;
        }
        return null;
    }

    public V get(K key) {
        var time = System.currentTimeMillis();
        var entry = values.get(key);
        if (entry != null) {
            entry.expiredTime = time + expiredTime;
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

    public static class Entry<V> {

        private long expiredTime;
        private final V value;

        public Entry(V value) {
            this.value = value;
        }
    }
}
