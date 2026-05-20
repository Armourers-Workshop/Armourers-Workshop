package moe.plushie.armourers_workshop.init.event.common;

import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockEvent {

    Entity entity();

    LevelAccessor level();

    BlockPos blockPos();

    @Nullable
    BlockState blockState();

    IBlockSnapshot snapshot();

    interface Place extends BlockEvent {
    }

    interface Destroy extends BlockEvent {
    }
}
