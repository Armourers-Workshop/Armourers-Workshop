package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntityProvider extends EntityBlock {

    BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState);

    //#if MC >= 11800
    //# @Override
    //# default BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        //# return createBlockEntity(null, blockPos, blockState);
    //# }
    //#else
    @Override
    default BlockEntity newBlockEntity(BlockGetter level) {
        return createBlockEntity(level, BlockPos.ZERO, null);
    }
    //#endif
}
