package moe.plushie.armourers_workshop.utils;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.data.IDataSerializerKey;

import java.util.function.Supplier;

public class DataSerializerKey<T> implements IDataSerializerKey<T> {

    private final String name;
    private final Codec<T> codec;
    private final T defaultValue;
    private final Supplier<T> constructor;

    public DataSerializerKey(String name, Codec<T> codec, T defaultValue, Supplier<T> constructor) {
        this.name = name;
        this.codec = codec;
        this.constructor = constructor;
        this.defaultValue = defaultValue;
    }

    public static <T> DataSerializerKey<T> create(String name, Codec<T> codec, T defaultValue) {
        return new DataSerializerKey<>(name, codec, defaultValue, null);
    }

    public static <T> DataSerializerKey<T> create(String name, Codec<T> codec, T defaultValue, Supplier<T> constructor) {
        return new DataSerializerKey<>(name, codec, defaultValue, constructor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    @Override
    public Supplier<T> getConstructor() {
        return constructor;
    }

    @Override
    public T getDefault() {
        return defaultValue;
    }
}
