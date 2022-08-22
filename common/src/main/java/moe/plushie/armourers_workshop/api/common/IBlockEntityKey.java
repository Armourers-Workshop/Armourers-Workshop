package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntityKey<T extends BlockEntity> extends IRegistryKey<BlockEntityType<T>> {

    T create(BlockGetter level, BlockPos blockPos, BlockState blockState);
}
