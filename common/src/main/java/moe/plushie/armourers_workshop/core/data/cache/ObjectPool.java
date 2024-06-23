package moe.plushie.armourers_workshop.core.data.cache;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public class ObjectPool<T> {

    private final Supplier<T> creator;
    private final ArrayDeque<T> values = new ArrayDeque<>();

    public ObjectPool(Supplier<T> creator) {
        this.creator = creator;
    }

    public void add(T value) {
        values.push(value);
    }

    public T get() {
        T value = values.poll();
        if (value != null) {
            return value;
        }
        return creator.get();
    }
}
