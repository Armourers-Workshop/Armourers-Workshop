package moe.plushie.armourers_workshop.compatibility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface AbstractBlockEntityProvider extends EntityBlock {

    BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState);

    @Override
    default BlockEntity newBlockEntity(BlockGetter level) {
        return createBlockEntity(level, BlockPos.ZERO, null);
    }
}
