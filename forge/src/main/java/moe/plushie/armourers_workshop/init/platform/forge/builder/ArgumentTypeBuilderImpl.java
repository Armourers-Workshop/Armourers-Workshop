package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;

public class ArgumentTypeBuilderImpl<T extends IArgumentType<?>> implements IArgumentTypeBuilder<T> {

    private final AbstractForgeArgumentTypeBuilder<T> builder;

    public ArgumentTypeBuilderImpl(IArgumentSerializer<T> serializer) {
        this.builder = new AbstractForgeArgumentTypeBuilder<>(serializer);
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.COMMAND_ARGUMENT_TYPES.register(name, builder::build);
    }
}
