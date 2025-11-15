package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compat.builder.AbstractArgumentTypeBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

@Available("[1.19, )")
public class AbstractFabricArgumentTypeBuilder<T extends IArgumentType<?>> extends AbstractArgumentTypeBuilder<T> {

    public AbstractFabricArgumentTypeBuilder(IArgumentSerializer<T> serializer) {
        super(serializer);
    }

    @Override
    protected void register(OpenResourceLocation registryName, AbstractArgumentTypeInfo<T> info) {
        ArgumentTypeRegistry.registerArgumentType(registryName.toLocation(), info.type(), info);
    }
}
