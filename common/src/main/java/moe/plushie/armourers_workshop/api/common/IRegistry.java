package moe.plushie.armourers_workshop.api.common;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface IRegistry<T> {

    int getId(ResourceLocation registryName);

    ResourceLocation getKey(T object);

    T getValue(ResourceLocation registryName);

    <I extends T> Supplier<I> register(String name, Supplier<? extends I> provider);
}
