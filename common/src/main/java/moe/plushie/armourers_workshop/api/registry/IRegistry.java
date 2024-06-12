package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IRegistry<T> {

    Collection<IRegistryHolder<? extends T>> getEntries();

    IResourceLocation getKey(T object);

    T getValue(IResourceLocation registryName);

    Class<?> getType();


    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param provider  A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    <I extends T> IRegistryHolder<I> register(String name, Supplier<? extends I> provider);
}
