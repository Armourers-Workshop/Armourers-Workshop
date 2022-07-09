package moe.plushie.armourers_workshop.builder.item.impl;

import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;

public interface IPaintToolSelector {

    void encode(final PacketBuffer buffer);

    void forEach(ItemUseContext context, BiConsumer<BlockPos, Direction> consumer);

    interface Provider {
        IPaintToolSelector createPaintToolSelector(ItemUseContext context);
    }
}
