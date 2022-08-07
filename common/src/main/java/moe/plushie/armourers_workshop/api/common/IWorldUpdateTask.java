package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IWorldUpdateTask {

    Level getLevel();

    BlockPos getBlockPos();

    BlockState getBlockState();

    InteractionResult run(Level level);
}

