package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IBlockEntitySupplier<T extends BlockEntity> {

    T create(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState);
}
