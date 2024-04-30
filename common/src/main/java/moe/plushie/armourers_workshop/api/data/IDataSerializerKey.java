package moe.plushie.armourers_workshop.api.data;

import com.mojang.serialization.Codec;

import java.util.function.Supplier;

public interface IDataSerializerKey<T> {

    String getName();

    Codec<T> getCodec();

    Supplier<T> getConstructor();

    T getDefault();
}
