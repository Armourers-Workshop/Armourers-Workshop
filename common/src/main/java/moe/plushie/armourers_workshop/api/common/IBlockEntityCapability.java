package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockEntityCapability<T> {

    T get(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable IDirection dir);
}
