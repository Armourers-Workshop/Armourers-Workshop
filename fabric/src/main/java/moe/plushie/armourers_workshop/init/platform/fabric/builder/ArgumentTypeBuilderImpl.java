package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricArgumentType;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;

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
        var registryName = ModConstants.key(name);
        ModLog.debug("Registering Argument Type '{}'", registryName);
        AbstractFabricArgumentType.register(registryName, argumentType, argumentSerializer.get());
        return TypedRegistry.Entry.ofValue(registryName, null);
    }
}
