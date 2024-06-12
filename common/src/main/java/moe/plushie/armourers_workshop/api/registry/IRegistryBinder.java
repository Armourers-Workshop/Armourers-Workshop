package moe.plushie.armourers_workshop.api.registry;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IRegistryBinder<T> {

    Consumer<IRegistryHolder<T>> get();

    static <T> Supplier<Runnable> perform(IRegistryBinder<T> binder, IRegistryHolder<T> value) {
        if (binder != null) {
            return () -> () -> binder.get().accept(value);
        }
        return null;
    }
}
