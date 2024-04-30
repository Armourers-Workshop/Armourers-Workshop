package moe.plushie.armourers_workshop.init.platform.event.common;

import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockEvent {

    Entity getEntity();

    LevelAccessor getLevel();

    BlockPos getPos();

    @Nullable
    BlockState getState();

    IBlockSnapshot getSnapshot();

    interface Break extends BlockEvent {
    }

    interface Place extends BlockEvent {
    }
}
