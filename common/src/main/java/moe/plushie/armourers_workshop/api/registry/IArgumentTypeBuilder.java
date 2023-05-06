package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;

import java.util.function.Supplier;

public interface IArgumentTypeBuilder<T extends IArgumentType<?>> extends IRegistryBuilder<T> {

    IArgumentTypeBuilder<T> serializer(Supplier<IArgumentSerializer<T>> argumentSerializer);
}
