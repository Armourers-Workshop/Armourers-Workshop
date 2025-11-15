package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compat.fabric.builder.AbstractFabricArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;

public class ArgumentTypeBuilderImpl<T extends IArgumentType<?>> implements IArgumentTypeBuilder<T> {

    private final AbstractFabricArgumentTypeBuilder<T> builder;

    public ArgumentTypeBuilderImpl(IArgumentSerializer<T> serializer) {
        this.builder = new AbstractFabricArgumentTypeBuilder<>(serializer);
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.COMMAND_ARGUMENT_TYPES.register(name, builder::build);
    }
}
