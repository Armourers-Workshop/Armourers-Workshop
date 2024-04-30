package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedRegistry<T> implements IRegistry<T> {

    private static final ArrayList<TypedRegistry<?>> INSTANCES = new ArrayList<>();

    private final Class<?> type;
    private final ArrayList<IRegistryKey<? extends T>> entries = new ArrayList<>();
    private final String typeName;

    private final KeyProvider<T> keyProvider;
    private final ValueProvider<T> valueProvider;
    private final RegisterProvider<T> registerProvider;

    public TypedRegistry(Class<?> type, KeyProvider<T> keyProvider, ValueProvider<T> valueProvider, RegisterProvider<T> registerProvider) {
        this.type = type;
        this.typeName = ObjectUtils.readableName(type);
        this.keyProvider = keyProvider;
        this.valueProvider = valueProvider;
        this.registerProvider = registerProvider;
        // we need found it.
        INSTANCES.add(this);
    }

    public static <T> TypedRegistry<T> create(Class<?> type, Registry<T> registry) {
        return new TypedRegistry<>(type, registry::getKey, registry::get, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(ResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                Registry.register(registry, registryName, value);
                return () -> value;
            }
        });
    }

    public static <T> TypedRegistry<T> factory(Class<? extends T> type, Function<ResourceLocation, T> factory) {
        return new TypedRegistry<>(type, null, null, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(ResourceLocation registryName, Supplier<? extends I> provider) {
                T value = factory.apply(registryName);
                // noinspection unchecked
                return () -> (I) value;
            }
        });
    }

    public static <T> TypedRegistry<T> map(Class<? extends T> type, BiConsumer<ResourceLocation, T> consumer) {
        return new TypedRegistry<>(type, null, null, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(ResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                consumer.accept(registryName, value);
                return () -> value;
            }
        });
    }

    public static <T> TypedRegistry<T> passthrough(Class<?> type) {
        return new TypedRegistry<>(type, null, null, new RegisterProvider<T>() {
            @Override
            public <I extends T> Supplier<I> register(ResourceLocation registryName, Supplier<? extends I> provider) {
                I value = provider.get();
                return () -> value;
            }
        });
    }


    public static <T> ResourceLocation findKey(T value) {
        for (TypedRegistry<?> registry : INSTANCES) {
            if (registry.getType().isInstance(value)) {
                TypedRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getKey(value);
            }
        }
        return new ResourceLocation("air");
    }

    public static <T> Collection<IRegistryKey<? extends T>> findEntries(Class<T> clazz) {
        for (TypedRegistry<?> registry : INSTANCES) {
            if (clazz.isAssignableFrom(registry.getType())) {
                TypedRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getEntries();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <I extends T> IRegistryKey<I> register(String name, Supplier<? extends I> provider) {
        ResourceLocation registryName = ModConstants.key(name);
        Supplier<I> object = registerProvider.register(registryName, provider);
        IRegistryKey<I> entry = Entry.of(registryName, object);
        entries.add(entry);
        ModLog.debug("Registering {} '{}'", typeName, registryName);
        return entry;
    }

    @Override
    public T getValue(ResourceLocation registryName) {
        return valueProvider.apply(registryName);
    }

    @Override
    public ResourceLocation getKey(T object) {
        return keyProvider.apply(object);
    }

    @Override
    public ArrayList<IRegistryKey<? extends T>> getEntries() {
        return entries;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    public static class Entry<T> implements IRegistryKey<T> {

        private final Supplier<T> value;
        private final ResourceLocation registryName;

        public Entry(ResourceLocation registryName, Supplier<T> value) {
            this.value = value;
            this.registryName = registryName;
        }

        public static <T> Entry<T> of(ResourceLocation registryName, Supplier<T> value) {
            return new Entry<>(registryName, value);
        }

        public static <T extends S, S> Entry<T> cast(ResourceLocation registryName, Supplier<S> value) {
            Supplier<T> targetValue = ObjectUtils.unsafeCast(value);
            return new Entry<>(registryName, targetValue);
        }

        public static <T> Entry<T> ofValue(ResourceLocation registryName, T value) {
            return of(registryName, () -> value);
        }

        public static <T extends S, S> Entry<T> castValue(ResourceLocation registryName, S value) {
            return cast(registryName, () -> value);
        }

        @Override
        public ResourceLocation getRegistryName() {
            return registryName;
        }

        @Override
        public T get() {
            return value.get();
        }
    }

    public interface KeyProvider<T> extends Function<T, ResourceLocation> {
    }

    public interface ValueProvider<T> extends Function<ResourceLocation, T> {
    }

    public interface RegisterProvider<T> {

        <I extends T> Supplier<I> register(ResourceLocation registryName, Supplier<? extends I> provider);
    }
}

