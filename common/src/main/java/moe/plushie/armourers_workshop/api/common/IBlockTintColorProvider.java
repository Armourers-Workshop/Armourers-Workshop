package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockTintColorProvider {

    int getTintColor(BlockState blockState, @Nullable BlockGetter reader, @Nullable BlockPos blockPos, int index);
}
