package moe.plushie.armourers_workshop.api.common;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

import javax.annotation.Nullable;

public interface IBlockTintColorProvider {

    int getTintColor(BlockState blockState, @Nullable IBlockDisplayReader reader, @Nullable BlockPos blockPos, int index);
}
