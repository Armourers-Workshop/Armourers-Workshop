package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

public class LoadableHashMap<K, V> {

    private final BiConsumer<K, Entry<V>> handler;
    private final ConcurrentHashMap<K, Entry<V>> values = new ConcurrentHashMap<>();

    public LoadableHashMap(BiConsumer<K, Entry<V>> handler) {
        this.handler = handler;
    }

    public void clear() {
        values.clear();
    }

    public Entry<V> getOrCreate(K key) {
        var task = values.get(key);
        if (task != null) {
            return task;
        }
        task = new Entry<V>();
        values.put(key, task);
        handler.accept(key, task);
        return task;
    }

    public static class Entry<V> implements IResultHandler<V> {

        private V value;
        private Throwable exception;
        private boolean isCompleted = false;

        private ConcurrentLinkedDeque<IResultHandler<V>> handlers = new ConcurrentLinkedDeque<>();

        @Override
        public void apply(V value, Throwable exception) {
            this.value = value;
            this.exception = exception;
            this.isCompleted = true;
            this.invoke();
        }

        public void invoke() {
            if (this.handlers.isEmpty()) {
                return;
            }
            var handlers = this.handlers;
            this.handlers = new ConcurrentLinkedDeque<>();
            handlers.forEach(handler -> handler.apply(value, exception));
        }

        public void listen(@Nullable IResultHandler<V> handler) {
            if (isCompleted) {
                if (handler != null) {
                    handler.apply(value, exception);
                }
                return;
            }
            if (handler != null) {
                handlers.add(handler);
            }
        }

        public V get() {
            return value;
        }
    }

}
