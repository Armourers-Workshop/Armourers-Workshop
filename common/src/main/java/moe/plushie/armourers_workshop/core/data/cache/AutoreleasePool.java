package moe.plushie.armourers_workshop.core.data.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class AutoreleasePool<T> {

    private static final ArrayList<AutoreleasePool<?>> POOLS = new ArrayList<>();

    private final Supplier<T> creator;
    private final Deque<T> reusable;
    private final ThreadLocal<Page<T>> autoreleasePages = ThreadLocal.withInitial(Page::new);

    protected AutoreleasePool(Supplier<T> creator, boolean isConcurrent) {
        this.creator = creator;
        if (isConcurrent) {
            this.reusable = new ConcurrentLinkedDeque<>();
        } else {
            this.reusable = new ArrayDeque<>();
        }
    }

    public static <T> AutoreleasePool<T> create(Supplier<T> creator) {
        return create(creator, false);
    }

    public static <T> AutoreleasePool<T> create(Supplier<T> creator, boolean isConcurrent) {
        var pool = new AutoreleasePool<>(creator, isConcurrent);
        POOLS.add(pool);
        return pool;
    }

    public static void begin() {
        POOLS.forEach(AutoreleasePool::beginCapturing);
    }

    public static void end() {
        POOLS.forEach(AutoreleasePool::endCapturing);
    }


    protected void beginCapturing() {
        autoreleasePages.get().begin(this);
    }

    protected void endCapturing() {
        autoreleasePages.get().end(this);
    }

    protected void recycle(List<T> objects) {
        // when too many available, we will pause recycling.
        int recycled = objects.size();
        int available = reusable.size();
        if (available < recycled * 2) {
            reusable.addAll(objects);
        }
    }

    public T get() {
        var page = autoreleasePages.get();
        var value = reusable.poll();
        if (value == null) {
            value = creator.get();
        }
        page.track(value);
        return value;
    }

    protected static class Page<T> {

        private ArrayList<T> releasing;
        private final ArrayList<T> queue = new ArrayList<>();

        public void begin(AutoreleasePool<T> pool) {
            releasing = queue;
        }

        public void track(T value) {
            if (releasing != null) {
                releasing.add(value);
            }
        }

        public void end(AutoreleasePool<T> pool) {
            releasing = null;
            if (queue.isEmpty()) {
                return;
            }
            pool.recycle(queue);
            queue.clear();
        }
    }
}
