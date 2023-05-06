package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public interface IBlockEntityType<T extends BlockEntity> extends Supplier<BlockEntityType<T>> {

    T create(BlockGetter level, BlockPos blockPos, BlockState blockState);

    interface Serializer<T extends BlockEntity> {
        T create(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState);
    }
}
