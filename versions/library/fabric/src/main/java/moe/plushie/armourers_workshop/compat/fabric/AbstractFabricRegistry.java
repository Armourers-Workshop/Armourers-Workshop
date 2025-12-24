package moe.plushie.armourers_workshop.compat.fabric;


import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceLocation;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.core.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.16, 1.26)")
public class AbstractFabricRegistry<T> implements TypedProvider<T> {

    private final Registry<T> registry;

    protected AbstractFabricRegistry(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> TypedProvider<T> from(Registry<T> registry) {
        return new AbstractFabricRegistry<>(registry);
    }

    @Override
    public <I extends T> Supplier<I> register(OpenResourceLocation registryName, Function<OpenResourceLocation, ? extends I> provider) {
        I value = provider.apply(registryName);
        Registry.register(registry, registryName.get(), value);
        return () -> value;
    }

    @Override
    public OpenResourceLocation getKey(T object) {
        return Objects.flatMap(registry.getKey(object), AbstractResourceLocation::wrap);
    }

    @Override
    public T getValue(OpenResourceLocation registryName) {
        return registry.get(registryName.get());
    }
}
