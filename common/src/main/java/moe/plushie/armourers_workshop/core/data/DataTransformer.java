package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class DataTransformer<K, V, T> {

    private final ExecutorService mainThread;
    private final ExecutorService transformThread;

    private final LoadHandler<K, T> loader;
    private final TransformHandler<K, T, V> transformer;

    private final Validator validator = new Validator();

    private final LinkedList<Entry> loadQueue = new LinkedList<>();
    private final LinkedList<Entry> transformQueue = new LinkedList<>();
    private final ConcurrentHashMap<K, Entry> allEntries = new ConcurrentHashMap<>();

    private final AtomicInteger loading = new AtomicInteger(0);
    private final AtomicInteger transforming = new AtomicInteger(0);

    private final int maxLoadCount;
    private final int maxTransformCount;

    public DataTransformer(ThreadFactory config, LoadHandler<K, T> loader, TransformHandler<K, T, V> transformer, int maxLoadCount, int maxTransformCount) {
        this.mainThread = ThreadUtils.newFixedThreadPool(1, config);
        this.transformThread = ThreadUtils.newFixedThreadPool(maxTransformCount, config);
        this.loader = loader;
        this.transformer = transformer;
        this.maxLoadCount = maxLoadCount;
        this.maxTransformCount = maxTransformCount;
    }

    public void remove(K key) {
        allEntries.remove(key);
    }

    @Nullable
    public Pair<V, Exception> get(K key) {
        Entry entry = getEntry(key);
        if (entry != null) {
            return entry.transformedData;
        }
        return null;
    }

    @Nullable
    public Pair<V, Exception> getOrLoad(K key, Ticket ticket) {
        Entry entry = getEntryAndCreate(key);
        if (!entry.isCompleted()) {
            load(key, ticket, null);
        }
        return entry.transformedData;
    }

    public void load(K key, Ticket ticket, IResultHandler<V> resultHandler) {
        Entry entry = getEntryAndCreate(key);
        validator.update(key, ticket);
        entry.listen(resultHandler);
        if (entry.isCompleted()) {
            return;
        }
        entry.elevate(ticket.priority(key));
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
            Entry entry = getLastTask(loadQueue);
            if (entry == null) {
                break;
            }
            if (entry.isCompleted()) {
                continue;
            }
            if (!validator.test(entry.key)) {
                abort(entry);
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
            Entry entry = getLastTask(transformQueue);
            if (entry == null) {
                break;
            }
            if (entry.isCompleted()) {
                continue;
            }
            T value = entry.getLoadedValue();
            if (value == null || !validator.test(entry.key)) {
                abort(entry);
                continue;
            }
            transform(value, entry);
            break;
        }
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

    private void abort(Entry entry) {
        entry.abort();
        allEntries.remove(entry.key);
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
        for (Entry entry : queue) {
            if (lastEntry == null || entry.priority > lastEntry.priority) {
                lastEntry = entry;
            }
        }
        queue.remove(lastEntry);
        return lastEntry;
    }

    protected class Entry {

        private final K key;

        private ArrayList<IResultHandler<V>> callbacks;

        private Pair<T, Exception> loadedData;
        private Pair<V, Exception> transformedData;

        private float priority = 0;

        private boolean isLoading = false;
        private boolean isTransforming = false;

        Entry(K key) {
            this.key = key;
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

        public void receiveLoadResult(T value, Exception exception) {
            this.loadedData = Pair.of(value, exception);
            this.isLoading = false;
        }

        public void receiveTransformResult(V value, Exception exception) {
            this.transformedData = Pair.of(value, exception);
            this.isTransforming = false;
            this.sendNotify();
        }

        public void abort() {
            this.transformedData = Pair.of(null, new RuntimeException("abort"));
            this.sendNotify();
        }

        public void sendNotify() {
            ArrayList<IResultHandler<V>> callbacks = this.callbacks;
            this.callbacks = null;
            if (callbacks == null || callbacks.isEmpty()) {
                return;
            }
            for (IResultHandler<V> callback : callbacks) {
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

        public boolean isCompleted() {
            return transformedData != null;
        }
    }

    public class Validator implements Predicate<K> {

        private final LinkedList<WeakReference<Ticket>> tickets = new LinkedList<>();

        @Override
        public synchronized boolean test(K key) {
            for (WeakReference<Ticket> ticket : tickets) {
                Ticket oldTicket = ticket.get();
                if (oldTicket != null) {
                    if (oldTicket.contains(key)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public synchronized void update(K key, Ticket ticket) {
            ticket.add(key);
            addTicket(ticket);
        }

        private void addTicket(Ticket ticket) {
            Iterator<WeakReference<Ticket>> iterator = tickets.iterator();
            while (iterator.hasNext()) {
                Ticket oldTicket = iterator.next().get();
                if (oldTicket == ticket) {
                    return; // yep, we found the old ticket.
                }
                if (oldTicket == null) {
                    iterator.remove();
                }
            }
            tickets.add(new WeakReference<>(ticket));
        }
    }

    public static class Builder<K, V, T> {

        private int maxLoadCount = 4;
        private int maxTransformCount = 4;

        private ThreadFactory configure;

        private LoadHandler<K, T> loader;
        private TransformHandler<K, T, V> transformer;

        public Builder<K, V, T> thread(String name, int newPriority) {
            this.configure = r -> {
                Thread thread = new Thread(r, name);
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

        public DataTransformer<K, V, T> build() {
            return new DataTransformer<>(configure, loader, transformer, maxLoadCount, maxTransformCount);
        }
    }

    public interface LoadHandler<T1, T2> {

        void accept(T1 t1, IResultHandler<T2> t2);
    }

    public interface TransformHandler<T1, T2, T3> {

        void accept(T1 t1, T2 t2, IResultHandler<T3> t3);

    }
}
