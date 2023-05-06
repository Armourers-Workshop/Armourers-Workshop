package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class AbstractForgeRegistryEntry<T> implements IRegistryKey<T> {

    private final Supplier<T> value;
    private final ResourceLocation registryName;

    public AbstractForgeRegistryEntry(ResourceLocation registryName, Supplier<T> value) {
        this.value = value;
        this.registryName = registryName;
    }

    public static <T> AbstractForgeRegistryEntry<T> of(ResourceLocation registryName, Supplier<T> value) {
        return new AbstractForgeRegistryEntry<>(registryName, value);
    }

    public static <T extends S, S> AbstractForgeRegistryEntry<T> cast(ResourceLocation registryName, Supplier<S> value) {
        Supplier<T> targetValue = ObjectUtils.unsafeCast(value);
        return new AbstractForgeRegistryEntry<>(registryName, targetValue);
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
