package moe.plushie.armourers_workshop.core.data.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public final class AutoreleasePool<T extends AutoreleasePool.Lifecycle> {

    private static final Collection<AutoreleasePool<?>> POOLS = new ConcurrentLinkedDeque<>();

    private final ThreadLocal<T> lifecycles;

    public AutoreleasePool(Supplier<T> supplier) {
        lifecycles = ThreadLocal.withInitial(supplier);
        POOLS.add(this);
    }

    public static void begin() {
        POOLS.forEach(it -> it.get().begin());
    }

    public static void end() {
        POOLS.forEach(it -> it.get().end());
    }

    public T get() {
        return lifecycles.get();
    }

    public interface Lifecycle {

        void begin();

        void end();
    }
}
