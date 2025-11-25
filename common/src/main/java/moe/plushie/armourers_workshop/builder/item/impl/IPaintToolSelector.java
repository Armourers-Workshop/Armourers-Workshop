package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;

import java.util.function.BiConsumer;

public interface IPaintToolSelector {

    void encode(final IFriendlyByteBuf buffer);

    void forEach(IUseOnContext context, BiConsumer<BlockPos, OpenDirection> consumer);

    interface Provider {
        IPaintToolSelector createPaintToolSelector(IUseOnContext context);
    }
}
