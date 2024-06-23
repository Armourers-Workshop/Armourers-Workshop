package moe.plushie.armourers_workshop.core.data.cache;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FlexibleQueue<T> {

    private final Supplier<T> creator;
    private final ArrayList<T> values = new ArrayList<>();

    private int used = 0;
    private int total = 0;

    public FlexibleQueue(Supplier<T> creator) {
        this.creator = creator;
    }

    public void forEach(Consumer<? super T> consumer) {
        for (int i = 0; i < used; i++) {
            var value = values.get(i);
            consumer.accept(value);
        }
    }

    public void clear() {
        used = 0;
    }

    public T get() {
        if (used < total) {
            return values.get(used++);
        }
        var value = creator.get();
        values.add(value);
        total += 1;
        used += 1;
        return value;
    }
}

