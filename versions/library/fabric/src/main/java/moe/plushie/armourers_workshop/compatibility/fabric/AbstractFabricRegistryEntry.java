package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;

public class AbstractFabricRegistryEntry<T> implements IRegistryKey<T> {

    private final T value;
    private final ResourceLocation registryName;

    public AbstractFabricRegistryEntry(ResourceLocation registryName, T value) {
        this.value = value;
        this.registryName = registryName;
    }

    public static <T> AbstractFabricRegistryEntry<T> of(ResourceLocation registryName, T value) {
        return new AbstractFabricRegistryEntry<>(registryName, value);
    }

    public static <T extends S, S> AbstractFabricRegistryEntry<T> cast(ResourceLocation registryName, S value) {
        T targetValue = ObjectUtils.unsafeCast(value);
        return new AbstractFabricRegistryEntry<>(registryName, targetValue);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public T get() {
        return value;
    }
}
