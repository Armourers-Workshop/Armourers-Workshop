package moe.plushie.armourers_workshop.api.core;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.plushie.armourers_workshop.compat.core.data.AbstractDataCodec;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface IDataCodec<A> {

    Codec<A> codec();

    static <T> IDataCodec<T> wrap(Codec<T> codec) {
        return () -> codec;
    }

    static <F, S> IDataCodec<Pair<F, S>> pair(IDataCodec<F> first, IDataCodec<S> second) {
        return wrap(Codec.pair(first.codec(), second.codec()));
    }

    static <F, S> IDataCodec<Either<F, S>> either(IDataCodec<F> first, IDataCodec<S> second) {
        return wrap(Codec.either(first.codec(), second.codec()));
    }

    static <O> IDataCodec<O> create(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return wrap(RecordCodecBuilder.create(builder));
    }

    default <S> IDataCodec<S> xmap(Function<? super A, ? extends S> to, Function<? super S, ? extends A> from) {
        return wrap(codec().xmap(to, from));
    }

    default <S> IDataCodec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends A>> from) {
        return wrap(codec().flatXmap(to, from));
    }

    default <E> IDataCodec<E> dispatch(Function<? super E, ? extends A> type, Function<? super A, ? extends IDataMapCodec<? extends E>> targetCodec) {
        return wrap(AbstractDataCodec.dispatch(codec(), "type", type, targetCodec));
    }

    default <E> IDataCodec<E> dispatch(String key, Function<? super E, ? extends A> type, Function<? super A, ? extends IDataMapCodec<? extends E>> targetCodec) {
        return wrap(AbstractDataCodec.dispatch(codec(), key, type, targetCodec));
    }

    default <T> IDataCodec<A> alternative(IDataCodec<T> alternative, Function<T, A> converter) {
        return either(this, alternative).xmap(either -> either.map(it -> it, converter), Either::left);
    }

    default IDataCodec<A> validate(Function<A, DataResult<A>> checker) {
        return flatXmap(checker, checker);
    }

    default IDataCodec<List<A>> listOf() {
        return wrap(codec().listOf());
    }

    default IDataMapCodec<A> fieldOf(String name) {
        return IDataMapCodec.wrap(codec().fieldOf(name));
    }

    default IDataMapCodec<Optional<A>> optionalFieldOf(String name) {
        return IDataMapCodec.wrap(codec().optionalFieldOf(name));
    }

    default IDataMapCodec<A> optionalFieldOf(String name, A defaultValue) {
        return IDataMapCodec.wrap(codec().optionalFieldOf(name, defaultValue));
    }

    IDataCodec<Boolean> BOOL = wrap(Codec.BOOL);

    IDataCodec<Byte> BYTE = wrap(Codec.BYTE);

    IDataCodec<Short> SHORT = wrap(Codec.SHORT);

    IDataCodec<Integer> INT = wrap(Codec.INT);

    IDataCodec<Long> LONG = wrap(Codec.LONG);

    IDataCodec<Float> FLOAT = wrap(Codec.FLOAT);

    IDataCodec<Double> DOUBLE = wrap(Codec.DOUBLE);

    IDataCodec<String> STRING = wrap(Codec.STRING);

    IDataCodec<ByteBuffer> BYTE_BUFFER = wrap(Codec.BYTE_BUFFER);
}
