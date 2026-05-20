package moe.plushie.armourers_workshop.compat.fabric.core;


import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceKey;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.core.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

@Available("[16, 26)")
public class AbstractFabricRegistry<T> implements TypedProvider<T> {

    private final Registry<T> registry;

    protected AbstractFabricRegistry(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> TypedProvider<T> from(Registry<T> registry) {
        return new AbstractFabricRegistry<>(registry);
    }

    @Override
    public <I extends T> Supplier<I> register(OpenResourceKey registryName, Function<OpenResourceKey, ? extends I> provider) {
        I value = provider.apply(registryName);
        Registry.register(registry, registryName.get(), value);
        return () -> value;
    }

    @Override
    public OpenResourceKey getKey(T object) {
        return Objects.flatMap(registry.getKey(object), AbstractResourceKey::wrap);
    }

    @Override
    public T getValue(OpenResourceKey registryName) {
        return registry.get(registryName.get());
    }
}
