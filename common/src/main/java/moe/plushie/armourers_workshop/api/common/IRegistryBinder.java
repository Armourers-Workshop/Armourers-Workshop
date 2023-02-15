package moe.plushie.armourers_workshop.api.common;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IRegistryBinder<T> {

    Consumer<IRegistryKey<T>> get();

    static <T> Supplier<Runnable> of(IRegistryBinder<T> binder, IRegistryKey<T> value) {
        if (binder != null) {
            return () -> () -> binder.get().accept(value);
        }
        return null;
    }
}
