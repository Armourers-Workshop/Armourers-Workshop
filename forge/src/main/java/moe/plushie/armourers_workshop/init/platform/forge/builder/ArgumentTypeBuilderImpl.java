package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeArgumentType;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TypedRegistry;

import java.util.function.Supplier;

public class ArgumentTypeBuilderImpl<T extends IArgumentType<?>> implements IArgumentTypeBuilder<T> {

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
//        ModLog.debug("Registering Argument Type '{}'", registryName);
        AbstractForgeArgumentType.register(registryName, argumentType, argumentSerializer.get());
        return TypedRegistry.Entry.ofValue(registryName, null);
    }
}
