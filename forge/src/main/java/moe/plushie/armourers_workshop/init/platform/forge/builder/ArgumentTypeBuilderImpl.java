package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeArgumentType;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.function.Supplier;

public class ArgumentTypeBuilderImpl<T extends ArgumentType<?>> implements IArgumentTypeBuilder<T> {

    private final Class<T> argumentType;
    private Supplier<IArgumentSerializer<T>> argumentSerializer;

    public ArgumentTypeBuilderImpl(Class<T> argumentType) {
        this.argumentType = argumentType;
    }

    @Override
    public IArgumentTypeBuilder<T> serializer(Supplier<IArgumentSerializer<T>> argumentSerializer) {
        this.argumentSerializer = argumentSerializer;
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        IResourceLocation registryName = ModConstants.key(name);
        AbstractForgeArgumentType.register(registryName, argumentType, argumentSerializer.get());
        return TypedRegistry.Entry.ofValue(registryName, null);
    }
}
