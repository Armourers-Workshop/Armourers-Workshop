package moe.plushie.armourers_workshop.api.core;

import java.util.function.Supplier;

public interface IDataSerializerKey<T> {

    String name();

    T defaultValue();

    IDataCodec<T> codec();

    Supplier<T> constructor();


    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec) {
        return create(name, codec, null, null);
    }

    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec, T defaultValue) {
        return create(name, codec, defaultValue, null);
    }

    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec, T defaultValue, Supplier<T> constructor) {
        return new IDataSerializerKey<T>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }

            @Override
            public IDataCodec<T> codec() {
                return codec;
            }

            @Override
            public Supplier<T> constructor() {
                return constructor;
            }
        };
    }
}
