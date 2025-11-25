package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.utils.Executors;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DataTransformer<K, V, T> {

    private final ExecutorService mainThread;
    private final ExecutorService transformThread;
    private final Optional<ExecutorService> cleanThread;

    private final LoadHandler<K, T> loader;
    private final TransformHandler<K, T, V> transformer;

    private final LinkedList<Entry> loadQueue = new LinkedList<>();
    private final LinkedList<Entry> transformQueue = new LinkedList<>();
    private final ConcurrentHashMap<K, Entry> allEntries = new ConcurrentHashMap<>();

    private final AtomicInteger loading = new AtomicInteger(0);
    private final AtomicInteger transforming = new AtomicInteger(0);

    private final int maxLoadCount;
    private final int maxTransformCount;

    protected DataTransformer(ThreadFactory config, LoadHandler<K, T> loader, TransformHandler<K, T, V> transformer, CleanHandler<K> cleaner, int maxLoadCount, int maxTransformCount) {
        this.mainThread = Executors.newFixedThreadPool(1, config);
        this.transformThread = Executors.newFixedThreadPool(maxTransformCount, config);
        this.cleanThread = createCleanThread(cleaner);
        this.loader = loader;
        this.transformer = transformer;
        this.maxLoadCount = maxLoadCount;
        this.maxTransformCount = maxTransformCount;
    }

    public void remove(K key) {
        var entry = allEntries.remove(key);
        if (entry != null && entry.isPending()) {
            entry.abort(new RuntimeException("user cancelled!"));
        }
    }

    @Nullable
    public Pair<V, Throwable> get(K key) {
        var entry = getEntry(key);
        if (entry != null) {
            return entry.transformedData;
        }
        return null;
    }

    @Nullable
    public Pair<V, Throwable> getOrLoad(Ticket<K> ticket) {
        var entry = getEntryAndCreate(ticket.get());
        if (!entry.isCompleted()) {
            load(ticket, null);
        } else {
            entry.updateTicket(ticket);
        }
        return entry.transformedData;
    }

    public void load(Ticket<K> ticket, IResultHandler<V> resultHandler) {
        var entry = getEntryAndCreate(ticket.get());
        entry.updateTicket(ticket);
        entry.listen(resultHandler);
        if (entry.isCompleted()) {
            return;
        }
        entry.elevate(ticket.priority());
        mainThread.execute(() -> {
            // check and add enqueue;
            if (!entry.isLoading) {
                entry.isLoading = true;
                loadQueue.add(entry);
            }
            dispatchIfNeeded();
        });
    }

    public void clear() {
        loadQueue.clear();
        transformQueue.clear();
        allEntries.clear();
        loading.set(0);
        transforming.set(0);
    }

    public void shutdown() {
        mainThread.shutdown();
        transformThread.shutdown();
        cleanThread.ifPresent(ExecutorService::shutdown);
        clear();
    }

    private void dispatchIfNeeded() {
        doLoadIfNeeded();
        doTransformIfNeeded();
    }

    private void doLoadIfNeeded() {
        // the run task is too much, wait.
        if (loading.get() >= maxLoadCount) {
            return;
        }
        while (true) {
            var entry = getLastTask(loadQueue);
            if (entry == null) {
                break;
            }
            if (entry.isCompleted()) {
                continue;
            }
            load(entry);
            break;
        }
    }

    private void doTransformIfNeeded() {
        // the run task is too much, wait.
        if (transforming.get() >= maxTransformCount) {
            return;
        }
        while (true) {
            var entry = getLastTask(transformQueue);
            if (entry == null) {
                break;
            }
            if (entry.isCompleted()) {
                continue;
            }
            // when the load is throw an error, ignore it.
            var error = entry.getLoadedError();
            if (error != null) {
                continue;
            }
            var value = entry.getLoadedValue();
            transform(value, entry);
            break;
        }
    }

    private void doCleanIfNeeded(Duration duration, Consumer<K> handler) {
        var startTime = System.currentTimeMillis() - duration.toMillis();
        var cleanQueue = new LinkedList<K>();
        allEntries.forEach((key, entry) -> {
            if (entry.expiredTime < 0) {
                if (!entry.checkTicket()) {
                    entry.expiredTime = System.currentTimeMillis();
                }
            } else if (entry.expiredTime < startTime) {
                cleanQueue.add(entry.key);
            }
        });
        cleanQueue.forEach(it -> {
            allEntries.remove(it);
            handler.accept(it);
        });
    }

    private void load(Entry entry) {
        loading.incrementAndGet();
        loader.accept(entry.key, (result, exception) -> mainThread.execute(() -> {
            loading.decrementAndGet();
            entry.receiveLoadResult(result, exception);
            // check and add enqueue;
            if (!entry.isTransforming) {
                entry.isTransforming = true;
                transformQueue.add(entry);
            }
            dispatchIfNeeded();
        }));
    }

    private void transform(T value, Entry entry) {
        transforming.incrementAndGet();
        transformThread.execute(() -> transformer.accept(entry.key, value, (result, exception) -> mainThread.execute(() -> {
            transforming.decrementAndGet();
            entry.receiveTransformResult(result, exception);
            dispatchIfNeeded();
        })));
    }

    private Entry getEntry(K key) {
        return allEntries.get(key);
    }

    private Entry getEntryAndCreate(K key) {
        return allEntries.computeIfAbsent(key, Entry::new);
    }

    @Nullable
    private Entry getLastTask(List<Entry> queue) {
        if (queue.isEmpty()) {
            return null;
        }
        Entry lastEntry = null;
        for (var entry : queue) {
            if (lastEntry == null || entry.priority > lastEntry.priority) {
                lastEntry = entry;
            }
        }
        queue.remove(lastEntry);
        return lastEntry;
    }

    private Optional<ExecutorService> createCleanThread(CleanHandler<K> cleanHandler) {
        // we need use the cleaner?
        if (cleanHandler == null) {
            return Optional.empty();
        }
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        var duration = cleanHandler.duration;
        var handler = cleanHandler.handler;
        var processRate = Math.max(duration.toMillis() / 2, 500);
        scheduledExecutor.scheduleAtFixedRate(() -> doCleanIfNeeded(duration, handler), 0, processRate, TimeUnit.MILLISECONDS);
        return Optional.of(scheduledExecutor);
    }

    private class Entry {

        private final K key;
        private final HashSet<Ticket<K>> tickets;

        private ArrayList<IResultHandler<V>> callbacks;

        private Pair<T, Throwable> loadedData;
        private Pair<V, Throwable> transformedData;

        private float priority = 0;
        private long expiredTime = 0;

        private boolean isLoading = false;
        private boolean isTransforming = false;

        Entry(K key) {
            this.key = key;
            this.tickets = new HashSet<>();
        }

        public synchronized void updateTicket(Ticket<K> ticket) {
            expiredTime = -1; // mark it never expires
            tickets.add(ticket);
        }

        public synchronized boolean checkTicket() {
            tickets.removeIf(Ticket::invalid);
            return !tickets.isEmpty();
        }

        public void elevate(float priority) {
            if (this.priority < priority) {
                this.priority = priority;
            }
        }

        public void listen(IResultHandler<V> callback) {
            if (callback == null) {
                return;
            }
            if (transformedData != null) {
                callback.apply(transformedData.getKey(), transformedData.getValue());
                return;
            }
            if (callbacks == null) {
                callbacks = new ArrayList<>();
            }
            callbacks.add(callback);
        }

        public void receiveLoadResult(T value, Throwable exception) {
            this.loadedData = Pair.of(value, exception);
            this.isLoading = false;
        }

        public void receiveTransformResult(V value, Throwable exception) {
            this.transformedData = Pair.of(value, exception);
            this.isTransforming = false;
            this.sendNotify();
        }

        public void abort(Exception exception) {
            this.transformedData = Pair.of(null, exception);
            this.sendNotify();
        }

        public void sendNotify() {
            var callbacks = this.callbacks;
            this.callbacks = null;
            if (callbacks == null || callbacks.isEmpty()) {
                return;
            }
            for (var callback : callbacks) {
                callback.apply(transformedData.getKey(), transformedData.getValue());
            }
        }

        @Nullable
        public T getLoadedValue() {
            if (loadedData != null) {
                return loadedData.getKey();
            }
            return null;
        }

        @Nullable
        public Throwable getLoadedError() {
            if (loadedData != null) {
                return loadedData.getValue();
            }
            return null;
        }

        public boolean isPending() {
            return isLoading || isTransforming;
        }

        public boolean isCompleted() {
            return transformedData != null;
        }
    }

    public static class Builder<K, V, T> {

        private int maxLoadCount = 4;
        private int maxTransformCount = 4;

        private ThreadFactory configure;

        private LoadHandler<K, T> loader;
        private TransformHandler<K, T, V> transformer;
        private CleanHandler<K> cleaner;

        public Builder<K, V, T> thread(String name, int newPriority) {
            this.configure = r -> {
                var thread = new Thread(r, name);
                thread.setPriority(newPriority);
                return thread;
            };
            return this;
        }

        public Builder<K, V, T> loadCount(int count) {
            this.maxLoadCount = count;
            return this;
        }

        public Builder<K, V, T> transformCount(int count) {
            this.maxTransformCount = count;
            return this;
        }

        public Builder<K, V, T> loader(LoadHandler<K, T> handler) {
            this.loader = handler;
            return this;
        }

        public Builder<K, V, T> transformer(TransformHandler<K, T, V> handler) {
            this.transformer = handler;
            return this;
        }

        public Builder<K, V, T> cleaner(Duration duration, Consumer<K> handler) {
            this.cleaner = new CleanHandler<>(duration, handler);
            return this;
        }

        public DataTransformer<K, V, T> build() {
            return new DataTransformer<>(configure, loader, transformer, cleaner, maxLoadCount, maxTransformCount);
        }
    }

    public interface LoadHandler<T1, T2> {

        void accept(T1 t1, IResultHandler<T2> t2);
    }

    public interface TransformHandler<T1, T2, T3> {

        void accept(T1 t1, T2 t2, IResultHandler<T3> t3);

    }

    protected static class CleanHandler<K> {

        private final Duration duration;
        private final Consumer<K> handler;

        public CleanHandler(Duration duration, Consumer<K> handler) {
            this.duration = duration;
            this.handler = handler;
        }
    }
}
