package moe.plushie.armourers_workshop.core.data.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class ObjectPool<T> {

    private final Supplier<T> creator;
    private final Deque<T> reusable;
    private final AutoreleasePool<Page> autoreleasePool;

    protected ObjectPool(Supplier<T> creator, boolean isConcurrent) {
        this.autoreleasePool = new AutoreleasePool<>(Page::new);
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

    protected void recycle(List<T> objects) {
        // when too many available, we will pause recycling.
        int recycled = objects.size();
        int available = reusable.size();
        if (available < recycled * 2) {
            reusable.addAll(objects);
        }
    }

    public T get() {
        var page = autoreleasePool.get();
        var value = reusable.poll();
        if (value == null) {
            value = creator.get();
        }
        page.track(value);
        return value;
    }

    protected class Page implements AutoreleasePool.Lifecycle {

        private List<T> releasing;
        private final List<T> queue = new ArrayList<>();

        @Override
        public void begin() {
            releasing = queue;
        }

        @Override
        public void end() {
            releasing = null;
            if (queue.isEmpty()) {
                return;
            }
            recycle(queue);
            queue.clear();
        }

        public void track(T value) {
            if (releasing != null) {
                releasing.add(value);
            }
        }
    }
}
