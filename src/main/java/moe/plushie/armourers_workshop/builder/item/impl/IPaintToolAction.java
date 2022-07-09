package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IPaintToolAction {

    void encode(final PacketBuffer buffer);

    void apply(World world, BlockPos pos, Direction dir, IPaintable provider, @Nullable PlayerEntity player);

    IPaintToolAction build(World world, BlockPos pos, Direction dir, IPaintable provider, @Nullable PlayerEntity player);
}
