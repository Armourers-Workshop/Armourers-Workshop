package moe.plushie.armourers_workshop.api.core;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IDataMapCodec<A> {

    MapCodec<A> mapCodec();

    static <O> IDataMapCodec<O> wrap(MapCodec<O> codec) {
        return () -> codec;
    }

    static <O> IDataMapCodec<O> create(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return IDataMapCodec.wrap(RecordCodecBuilder.mapCodec(builder));
    }

    static <A> IDataMapCodec<A> unit(A defaultValue) {
        return unit(() -> defaultValue);
    }

    static <A> IDataMapCodec<A> unit(Supplier<A> defaultValue) {
        return wrap(MapCodec.unit(defaultValue));
    }

    default <S> IDataMapCodec<S> xmap(Function<? super A, ? extends S> to, Function<? super S, ? extends A> from) {
        return wrap(mapCodec().xmap(to, from));
    }

    default <S> IDataMapCodec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends A>> from) {
        return wrap(mapCodec().flatXmap(to, from));
    }

    default IDataMapCodec<A> validate(Function<A, DataResult<A>> checker) {
        return flatXmap(checker, checker);
    }

    default IDataMapCodec<A> orElse(A value) {
        return wrap(mapCodec().orElse(value));
    }

    default IDataMapCodec<A> orElseGet(Supplier<? extends A> value) {
        return wrap(mapCodec().orElseGet(value));
    }

    default <O> RecordCodecBuilder<O, A> forGetter(Function<O, A> getter) {
        return RecordCodecBuilder.of(getter, mapCodec());
    }

    default IDataCodec<A> codec() {
        return IDataCodec.wrap(mapCodec().codec());
    }
}
