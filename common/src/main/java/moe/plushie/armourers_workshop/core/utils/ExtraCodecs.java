package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public interface ExtraCodecs {

    static <T extends IDataSerializable.Immutable> IDataCodec<T> serializable(Function<? super IDataSerializer, ? extends T> factory) {
        return serializable(COMPOUND_TAG, factory);
    }

    static <T extends IDataSerializable.Immutable> IDataCodec<T> serializable(IDataCodec<CompoundTag> codec, Function<? super IDataSerializer, ? extends T> factory) {
        return codec.xmap(tag -> {
            var serializer = new TagSerializer(tag);
            return factory.apply(serializer);
        }, it -> {
            var serializer = new TagSerializer();
            it.serialize(serializer);
            return serializer.tag();
        });
    }

    IDataCodec<UUID> UUID = IDataCodec.INT.listOf().xmap(it -> {
        var l = (long) it.get(0) << 32 | (long) it.get(1) & 0xffffffffL;
        var m = (long) it.get(2) << 32 | (long) it.get(3) & 0xffffffffL;
        return new UUID(l, m);
    }, it -> {
        var result = new ArrayList<Integer>();
        var l = it.getMostSignificantBits();
        var m = it.getLeastSignificantBits();
        result.add((int) (l >> 32));
        result.add((int) l);
        result.add((int) (m >> 32));
        result.add((int) m);
        return result;
    });


    IDataCodec<BlockPos> BLOCK_POS = IDataCodec.wrap(BlockPos.CODEC).alternative(IDataCodec.LONG, BlockPos::of);

    IDataCodec<GlobalPos> GLOBAL_POS = IDataCodec.wrap(GlobalPos.CODEC);

    IDataCodec<CompoundTag> COMPOUND_TAG = IDataCodec.wrap(CompoundTag.CODEC);

    IDataCodec<ItemStack> ITEM_STACK = IDataCodec.wrap(ItemStack.CODEC);
}
