package moe.plushie.armourers_workshop.utils;

import java.util.function.Supplier;

public class LazyValue<T> implements Supplier<T> {

    private T value;
    private Supplier<T> provider;

    public LazyValue(Supplier<T> provider) {
        this.provider = provider;
    }

    public static <T> LazyValue<T> of(Supplier<T> provider) {
        return new LazyValue<>(provider);
    }

    @Override
    public T get() {
        if (provider != null) {
            value = provider.get();
            provider = null;
        }
        return value;
    }
}
