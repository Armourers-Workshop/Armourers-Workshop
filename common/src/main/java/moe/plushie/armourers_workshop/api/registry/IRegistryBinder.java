package moe.plushie.armourers_workshop.api.registry;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IRegistryBinder<T> {

    Consumer<IRegistryKey<T>> get();

    static <T> Supplier<Runnable> perform(IRegistryBinder<T> binder, IRegistryKey<T> value) {
        if (binder != null) {
            return () -> () -> binder.get().accept(value);
        }
        return null;
    }
}
