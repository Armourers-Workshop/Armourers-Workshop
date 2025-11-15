package moe.plushie.armourers_workshop.api.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("unused")
public interface IDataCodecs<A> {

    IDataCodec<UUID> UUID = IDataCodec.INT.listOf().xmap(it -> {
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


    IDataCodec<BlockPos> BLOCK_POS = IDataCodec.wrap(BlockPos.CODEC).alternative(IDataCodec.LONG, BlockPos::of);

    IDataCodec<GlobalPos> GLOBAL_POS = IDataCodec.wrap(GlobalPos.CODEC);

    IDataCodec<CompoundTag> COMPOUND_TAG = IDataCodec.wrap(CompoundTag.CODEC);

    IDataCodec<ItemStack> ITEM_STACK = IDataCodec.wrap(ItemStack.CODEC);
}
