package moe.plushie.armourers_workshop.api.common;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Supplier;

public interface IRegistry<T> {

    int getId(ResourceLocation registryName);

    T getValue(ResourceLocation registryName);

    ResourceLocation getKey(T object);

    Collection<IRegistryKey<T>> getEntries();

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param provider  A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    <I extends T> IRegistryKey<I> register(String name, Supplier<? extends I> provider);
}
