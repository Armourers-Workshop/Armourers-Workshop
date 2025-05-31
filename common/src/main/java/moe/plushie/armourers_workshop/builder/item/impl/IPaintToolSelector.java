package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.BiConsumer;

public interface IPaintToolSelector {

    void encode(final IFriendlyByteBuf buffer);

    void forEach(UseOnContext context, BiConsumer<BlockPos, OpenDirection> consumer);

    interface Provider {
        IPaintToolSelector createPaintToolSelector(UseOnContext context);
    }
}
