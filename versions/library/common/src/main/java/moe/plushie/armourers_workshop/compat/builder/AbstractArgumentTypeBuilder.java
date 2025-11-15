package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compat.core.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public abstract class AbstractArgumentTypeBuilder<T extends IArgumentType<?>> {

    protected final IArgumentSerializer<T> serializer;

    public AbstractArgumentTypeBuilder(IArgumentSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public T build(OpenResourceLocation registryName) {
        var info = new AbstractArgumentTypeInfo<>(serializer.type(), serializer);
        register(registryName, info);
        return null; // only register.
    }

    protected abstract void register(OpenResourceLocation registryName, AbstractArgumentTypeInfo<T> info);
}
