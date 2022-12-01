package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;

import java.util.function.Supplier;

public interface IArgumentTypeBuilder<T extends IArgumentType<?>> extends IEntryBuilder<IRegistryKey<T>> {

    IArgumentTypeBuilder<T> serializer(Supplier<IArgumentSerializer<T>> argumentSerializer);
}
