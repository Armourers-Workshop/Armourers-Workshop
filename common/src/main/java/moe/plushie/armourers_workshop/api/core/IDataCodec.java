package moe.plushie.armourers_workshop.api.core;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface IDataCodec<A> {

    Codec<A> codec();

    static <T> IDataCodec<T> wrap(final Codec<T> codec) {
        return () -> codec;
    }

    static <T> IDataCodec.Field<T> wrap(final MapCodec<T> codec) {
        return () -> wrap(codec.codec());
    }

    static <F, S> IDataCodec<Pair<F, S>> pair(final IDataCodec<F> first, final IDataCodec<S> second) {
        return wrap(Codec.pair(first.codec(), second.codec()));
    }

    static <F, S> IDataCodec<Either<F, S>> either(final IDataCodec<F> first, final IDataCodec<S> second) {
        return wrap(Codec.either(first.codec(), second.codec()));
    }

    default IDataCodec<List<A>> listOf() {
        return wrap(codec().listOf());
    }

    default <S> IDataCodec<S> xmap(final Function<? super A, ? extends S> to, final Function<? super S, ? extends A> from) {
        return wrap(codec().xmap(to, from));
    }

    default <T> IDataCodec<A> alternative(IDataCodec<T> alternative, Function<T, A> converter) {
        return either(this, alternative).xmap(either -> either.map(v -> v, converter), Either::left);
    }

    default <T extends IDataSerializable.Immutable> IDataCodec<T> serializer(Function<? super IDataSerializer, ? extends T> factory) {
        return xmap(tag -> {
            var serializer = new TagSerializer((CompoundTag) tag);
            return factory.apply(serializer);
        }, it -> {
            var serializer = new TagSerializer();
            it.serialize(serializer);
            // noinspection unchecked
            return (A) serializer.getTag();
        });
    }

    default IDataCodec.Field<A> fieldOf(final String name) {
        return wrap(codec().fieldOf(name));
    }

    default IDataCodec.Field<Optional<A>> optionalFieldOf(final String name) {
        return wrap(codec().optionalFieldOf(name));
    }

    default IDataCodec.Field<A> optionalFieldOf(final String name, final A defaultValue) {
        return wrap(codec().optionalFieldOf(name, defaultValue));
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

    IDataCodec<UUID> UUID = INT.listOf().xmap(it -> {
        long l = (long) it.get(0) << 32 | (long) it.get(1) & 0xffffffffL;
        long m = (long) it.get(2) << 32 | (long) it.get(3) & 0xffffffffL;
        return new UUID(l, m);
    }, it -> {
        var result = new ArrayList<Integer>();
        long l = it.getMostSignificantBits();
        long m = it.getLeastSignificantBits();
        result.add((int) (l >> 32));
        result.add((int) l);
        result.add((int) (m >> 32));
        result.add((int) m);
        return result;
    });


    IDataCodec<BlockPos> BLOCK_POS = wrap(BlockPos.CODEC).alternative(LONG, BlockPos::of);

    IDataCodec<GlobalPos> GLOBAL_POS = wrap(GlobalPos.CODEC);

    IDataCodec<CompoundTag> COMPOUND_TAG = wrap(CompoundTag.CODEC);

    IDataCodec<ItemStack> ITEM_STACK = wrap(ItemStack.CODEC);


    interface Field<A> {

        IDataCodec<A> codec();
    }
}
