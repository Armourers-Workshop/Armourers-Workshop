package moe.plushie.armourers_workshop.core.data.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class ObjectPool<T> extends AutoreleasePool {

    private final Supplier<T> creator;
    private final Deque<T> reusable;
    private final ThreadLocal<Page<T>> autoreleasePages;

    protected ObjectPool(Supplier<T> creator, boolean isConcurrent) {
        this.autoreleasePages = ThreadLocal.withInitial(Page::new);
        this.creator = creator;
        if (isConcurrent) {
            this.reusable = new ConcurrentLinkedDeque<>();
        } else {
            this.reusable = new ArrayDeque<>();
        }
    }

    public static <T> ObjectPool<T> create(Supplier<T> creator) {
        return create(creator, false);
    }

    public static <T> ObjectPool<T> create(Supplier<T> creator, boolean isConcurrent) {
        return new ObjectPool<>(creator, isConcurrent);
    }

    @Override
    protected void beginCapturing() {
        autoreleasePages.get().begin(this);
    }

    @Override
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

        public void begin(ObjectPool<T> pool) {
            releasing = queue;
        }

        public void track(T value) {
            if (releasing != null) {
                releasing.add(value);
            }
        }

        public void end(ObjectPool<T> pool) {
            releasing = null;
            if (queue.isEmpty()) {
                return;
            }
            pool.recycle(queue);
            queue.clear();
        }
    }
}
