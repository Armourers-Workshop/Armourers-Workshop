package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compat.builder.AbstractArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;

@Available("[1.21, )")
public class AbstractForgeArgumentTypeBuilder<T extends IArgumentType<?>> extends AbstractArgumentTypeBuilder<T> {

    private static final TypedProvider<ArgumentTypeInfo<?, ?>> REGISTRY = AbstractForgeRegistry.from(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

    public AbstractForgeArgumentTypeBuilder(IArgumentSerializer<T> serializer) {
        super(serializer);
    }

    @Override
    protected void register(OpenResourceLocation registryName, AbstractArgumentTypeInfo<T> info) {
        ArgumentTypeInfos.registerByClass(info.type(), info);
        REGISTRY.register(registryName, it -> info);
    }
}
