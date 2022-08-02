package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IPaintToolAction {

    void encode(final FriendlyByteBuf buffer);

    void apply(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player);

    IPaintToolAction build(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player);
}
