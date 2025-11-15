package moe.plushie.armourers_workshop.core.utils;

import com.mojang.serialization.DataResult;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedRegistry<T> implements TypedProvider<T> {

    private final String name;
    private final TypedProvider<T> provider;

    private final ArrayList<TypedHolder<? extends T>> holders = new ArrayList<>();
    private final HashMap<OpenResourceLocation, TypedHolder<? extends T>> idToValue = new HashMap<>();

    public TypedRegistry(String name, TypedProvider<T> provider) {
        this.name = name;
        this.provider = provider;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name     The new entry's name, it will automatically have the modid prefixed.
     * @param supplier A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    public <I extends T> TypedHolder<I> register(String name, Function<OpenResourceLocation, ? extends I> supplier) {
        return register(ModConstants.key(name), supplier);
    }

    @Override
    public <I extends T> TypedHolder<I> register(OpenResourceLocation registryName, Function<OpenResourceLocation, ? extends I> supplier) {
        Supplier<I> object = provider.register(registryName, supplier);
        TypedHolder<I> entry = TypedHolder.of(registryName, object);
        holders.add(entry);
        idToValue.put(registryName, entry);
        ModLog.debug("Registering {} '{}'", this.name, registryName);
        return entry;
    }

    public void forEach(Consumer<? super TypedHolder<? extends T>> action) {
        holders.forEach(action);
    }

    @Override
    public T getValue(OpenResourceLocation registryName) {
        var holder = idToValue.get(registryName);
        if (holder != null) {
            return holder.get();
        }
        return provider.getValue(registryName);
    }

    @Override
    public OpenResourceLocation getKey(T value) {
        for (var holder : holders) {
            if (holder.get() == value) {
                return holder.registryName();
            }
        }
        return provider.getKey(value);
    }

    public Collection<TypedHolder<? extends T>> values() {
        return holders;
    }

    public Set<Map.Entry<OpenResourceLocation, TypedHolder<? extends T>>> entitySet() {
        return idToValue.entrySet();
    }

    public IDataCodec<T> codec() {
        return OpenResourceLocation.CODEC.flatXmap(key -> {
            var value = getValue(key);
            if (value == null) {
                return DataResult.error(() -> "Unknown element id: " + key);
            }
            return DataResult.success(value);
        }, value -> {
            var key = getKey(value);
            if (key == null) {
                return DataResult.error(() -> "Element with unknown id: " + value);
            }
            return DataResult.success(key);
        });
    }
}

