package moe.plushie.armourers_workshop.api.registry;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IRegistryHolder<T> extends IRegistryEntry, Supplier<T> {
}
