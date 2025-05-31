package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IPaintToolAction {

    void encode(final IFriendlyByteBuf buffer);

    void apply(Level level, BlockPos pos, OpenDirection dir, IBlockPaintable provider, @Nullable Player player);

    IPaintToolAction build(Level level, BlockPos pos, OpenDirection dir, IBlockPaintable provider, @Nullable Player player);
}
