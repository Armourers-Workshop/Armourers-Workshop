package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockTintSource {

    int calculate(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos);

    IDataMapCodec<? extends IBlockTintSource> type();
}
