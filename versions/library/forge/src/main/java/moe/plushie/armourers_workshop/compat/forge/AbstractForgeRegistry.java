package moe.plushie.armourers_workshop.compat.forge;


import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceLocation;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.16, )")
public class AbstractForgeRegistry<T> extends AbstractForgeRegistryImpl implements TypedProvider<T> {

    private final Function<T, ResourceLocation> keyProvider;
    private final Function<ResourceLocation, T> valueProvider;
    private final TypedProvider<T> registryProvider;

    public AbstractForgeRegistry(Function<T, ResourceLocation> keyProvider, Function<ResourceLocation, T> valueProvider, TypedProvider<T> registryProvider) {
        this.keyProvider = keyProvider;
        this.valueProvider = valueProvider;
        this.registryProvider = registryProvider;
    }

    @Override
    public <I extends T> Supplier<I> register(OpenResourceLocation registryName, Function<OpenResourceLocation, ? extends I> provider) {
        return registryProvider.register(registryName, provider);
    }

    @Override
    public OpenResourceLocation getKey(T object) {
        if (keyProvider != null) {
            return Objects.flatMap(keyProvider.apply(object), AbstractResourceLocation::wrap);
        }
        return null;
    }

    @Override
    public T getValue(OpenResourceLocation registryName) {
        if (valueProvider != null) {
            return valueProvider.apply(registryName.get());
        }
        return null;
    }
}
