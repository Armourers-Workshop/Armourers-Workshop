package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedRegistry<T> implements IRegistry<T> {

    private static final ArrayList<TypedRegistry<?>> INSTANCES = new ArrayList<>();

    private final Class<?> type;
    private final ArrayList<IRegistryHolder<? extends T>> entries = new ArrayList<>();
    private final String typeName;

    private final KeyProvider<T> keyProvider;
    private final ValueProvider<T> valueProvider;
    private final RegisterProvider<T> registerProvider;

    public TypedRegistry(String name, Class<?> type, KeyProvider<T> keyProvider, ValueProvider<T> valueProvider, RegisterProvider<T> registerProvider) {
        this.type = type;
        this.typeName = name;
        this.keyProvider = keyProvider;
        this.valueProvider = valueProvider;
        this.registerProvider = registerProvider;
        // we need found it.
        INSTANCES.add(this);
    }

    public static <T> TypedRegistry<T> create(String name, Class<?> type, Registry<T> registry) {
        return create(name, type, registry::getKey, registry::get, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(IResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                Registry.register(registry, registryName.toLocation(), value);
                return () -> value;
            }
        });
    }

    public static <T> TypedRegistry<T> create(String name, Class<?> type, Function<T, ResourceLocation> keyProvider, Function<ResourceLocation, T> valueProvider, RegisterProvider<T> registerProvider) {
        KeyProvider<T> keyProvider1 = value -> Objects.flatMap(keyProvider.apply(value), OpenResourceLocation::create);
        ValueProvider<T> valueProvider1 = key -> valueProvider.apply(key.toLocation());
        return new TypedRegistry<>(name, type, keyProvider1, valueProvider1, registerProvider);
    }

    public static <T> TypedRegistry<T> factory(String name, Class<? extends T> type, Function<IResourceLocation, T> factory) {
        return factory(name, type, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(IResourceLocation registryName, Supplier<? extends I> provider) {
                T value = factory.apply(registryName);
                // noinspection unchecked
                return () -> (I) value;
            }
        });
    }

    public static <T> TypedRegistry<T> factory(String name, Class<?> type, RegisterProvider<T> registerProvider) {
        return new TypedRegistry<>(name, type, null, null, registerProvider);
    }

    public static <T> TypedRegistry<T> map(String name, Class<? extends T> type, BiConsumer<IResourceLocation, T> consumer) {
        return new TypedRegistry<>(name, type, null, null, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(IResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                consumer.accept(registryName, value);
                return () -> value;
            }
        });
    }

    public static <T> TypedRegistry<T> passthrough(String name, Class<?> type) {
        return new TypedRegistry<>(name, type, null, null, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(IResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                return () -> value;
            }
        });
    }


    public static <T> IResourceLocation findKey(T value) {
        for (var registry : INSTANCES) {
            if (registry.type().isInstance(value)) {
                TypedRegistry<T> registry1 = Objects.unsafeCast(registry);
                return registry1.getKey(value);
            }
        }
        return OpenResourceLocation.create("minecraft", "air");
    }

    public static <T> Collection<IRegistryHolder<? extends T>> findEntries(Class<T> clazz) {
        for (var registry : INSTANCES) {
            if (clazz.isAssignableFrom(registry.type())) {
                TypedRegistry<T> registry1 = Objects.unsafeCast(registry);
                return registry1.entries();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <I extends T> IRegistryHolder<I> register(String name, Supplier<? extends I> provider) {
        var registryName = ModConstants.key(name);
        Supplier<I> object = registerProvider.register(registryName, provider);
        IRegistryHolder<I> entry = Entry.of(registryName, object);
        entries.add(entry);
        ModLog.debug("Registering {} '{}'", typeName, registryName);
        return entry;
    }

    @Override
    public T getValue(IResourceLocation registryName) {
        return valueProvider.apply(registryName);
    }

    @Override
    public IResourceLocation getKey(T object) {
        return keyProvider.apply(object);
    }

    @Override
    public List<IRegistryHolder<? extends T>> entries() {
        return entries;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    public static class Entry<T> implements IRegistryHolder<T> {

        private final Supplier<T> value;
        private final IResourceLocation registryName;

        public Entry(IResourceLocation registryName, Supplier<T> value) {
            this.value = value;
            this.registryName = registryName;
        }

        public static <T> Entry<T> of(IResourceLocation registryName, Supplier<T> value) {
            return new Entry<>(registryName, value);
        }

        public static <T extends S, S> Entry<T> cast(IResourceLocation registryName, Supplier<S> value) {
            Supplier<T> targetValue = Objects.unsafeCast(value);
            return new Entry<>(registryName, targetValue);
        }

        public static <T> Entry<T> ofValue(IResourceLocation registryName, T value) {
            return of(registryName, () -> value);
        }

        public static <T extends S, S> Entry<T> castValue(IResourceLocation registryName, S value) {
            return cast(registryName, () -> value);
        }

        @Override
        public IResourceLocation registryName() {
            return registryName;
        }

        @Override
        public T get() {
            return value.get();
        }
    }

    public interface KeyProvider<T> extends Function<T, IResourceLocation> {
    }

    public interface ValueProvider<T> extends Function<IResourceLocation, T> {
    }

    public interface RegisterProvider<T> {

        <I extends T> Supplier<I> register(IResourceLocation registryName, Supplier<? extends I> provider);
    }
}

