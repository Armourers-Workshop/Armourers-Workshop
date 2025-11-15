package moe.plushie.armourers_workshop.compat.core.data.serializer;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.Optional;

public abstract class AbstractDataSerializerImpl<A> implements IDataSerializer {

    protected final SerializationContext context;
    protected final DynamicOps<A> ops;

    protected AbstractDataSerializerImpl(DynamicOps<A> ops, SerializationContext context) {
        this.context = context;
        this.ops = context.create(ops);
    }

    protected abstract A readRootValue();

    protected abstract void writeRootValue(A value);

    protected abstract boolean hasValue(String key);

    protected abstract A readValue(String key);

    protected abstract void writeValue(String key, A value);

    protected <T> Optional<T> decodeValue(IDataCodec<T> codec, A data) {
        var result = codec.codec().decode(ops, data);
        result.ifError(it -> ModLog.warn("{}", it.message()));
        return result.result().map(Pair::getFirst);
    }

    protected <T> Optional<A> encodeValue(IDataCodec<T> codec, T value) {
        var result = codec.codec().encodeStart(ops, value);
        result.ifError(it -> ModLog.warn("{}", it.message()));
        return result.result();
    }

    public <T> T decode(IDataCodec<T> codec) {
        return decodeValue(codec, readRootValue()).orElse(null);
    }

    public <T> void encode(IDataCodec<T> codec, T value) {
        encodeValue(codec, value).ifPresent(AbstractDataSerializerImpl.this::writeRootValue);
    }

    @Override
    public <T> T read(IDataSerializerKey<T> key) {
        var name = key.name();
        if (hasValue(name)) {
            var value = decodeValue(key.codec(), readValue(key.name()));
            if (value.isPresent()) {
                return value.get();
            }
        }
        var constructor = key.constructor();
        if (constructor != null) {
            return constructor.get();
        }
        return key.defaultValue();
    }

    @Override
    public <T> void write(IDataSerializerKey<T> key, T value) {
        var defaultValue = key.defaultValue();
        if (defaultValue == value || Objects.equals(defaultValue, value)) {
            return;
        }
        encodeValue(key.codec(), value).ifPresent(it -> writeValue(key.name(), it));
    }

    public SerializationContext context() {
        return context;
    }
}
