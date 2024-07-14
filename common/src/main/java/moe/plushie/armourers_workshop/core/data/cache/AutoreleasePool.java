package moe.plushie.armourers_workshop.core.data.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AutoreleasePool {

    private static final Collection<AutoreleasePool> POOLS = new ConcurrentLinkedDeque<>();

    public static void begin() {
        POOLS.forEach(AutoreleasePool::beginCapturing);
    }

    public static void end() {
        POOLS.forEach(AutoreleasePool::endCapturing);
    }

    protected final void addToPool(AutoreleasePool pool) {
        POOLS.add(this);
    }

    protected abstract void beginCapturing();

    protected abstract void endCapturing();

}
