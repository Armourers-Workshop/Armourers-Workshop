package moe.plushie.armourers_workshop.core.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DataLoader<K, V> {

    private final BiConsumer<K, Consumer<Optional<V>>> loader;
    private final ExecutorService executor;

    private final HashMap<K, Optional<V>> caches = new HashMap<>();
    private final HashMap<K, ArrayList<Consumer<Optional<V>>>> loading = new HashMap<>();

    public DataLoader(ExecutorService executor, BiConsumer<K, Consumer<Optional<V>>> loader) {
        this.executor = executor;
        this.loader = loader;
    }

    @Nonnull
    public static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }


    public void put(K key, Optional<V> value) {
        synchronized (this) {
            caches.put(key, value);
        }
    }

    @Nullable
    public Optional<V> get(K key) {
        Optional<V> value;
        synchronized (this) {
            value = caches.get(key);
        }
        return value;
    }

    @Nullable
    public Optional<V> getOrLoad(K key) {
        Optional<V> value = get(key);
        if (value == null) {
            load(key, true, null);
            return get(key);
        }
        return value;
    }

    public void load(K key, boolean immediateTask, @Nullable Consumer<Optional<V>> complete) {
        Optional<V> value = get(key);
        if (value != null) {
            if (complete != null) {
                complete.accept(value);
            }
            return;
        }
        synchronized (this) {
            ArrayList<Consumer<Optional<V>>> tasks = loading.get(key);
            if (tasks != null) {
                if (complete != null) {
                    tasks.add(complete);
                }
                return;
            }
            tasks = new ArrayList<>();
            if (complete != null) {
                tasks.add(complete);
            }
            loading.put(key, tasks);
        }
        dispatch(key, immediateTask);
    }

    public void add(Runnable task) {
        if (executor != null) {
            executor.submit(task);
        } else {
            task.run();
        }
    }


    private void dispatch(K key, boolean immediateTask) {
        if (executor != null && !immediateTask) {
            executor.submit(() -> dispatch(key, true));
            return;
        }
        loader.accept(key, newValue -> {
            ArrayList<Consumer<Optional<V>>> tasks;
            synchronized (this) {
                caches.put(key, newValue);
                tasks = loading.remove(key);
            }
            if (tasks != null) {
                tasks.forEach(task -> task.accept(newValue));
            }
        });
    }

    public static class Builder<K, V> {
        private ExecutorService executor;

        public Builder<K, V> threadPool(int size) {
            this.executor = Executors.newFixedThreadPool(size);
            return this;
        }

        @Nonnull
        public <K1 extends K, V1 extends V> DataLoader<K1, V1> build(BiConsumer<K1, Consumer<Optional<V1>>> loader) {
            return new DataLoader<>(executor, loader);
        }
    }

    public void clear() {
        caches.clear();
        loading.clear();
    }
}
