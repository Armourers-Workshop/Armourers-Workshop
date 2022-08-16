package moe.plushie.armourers_workshop.api.common.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IBlockEntityProvider<T extends BlockEntity> {

    T create(BlockPos var1, BlockState var2);
}
