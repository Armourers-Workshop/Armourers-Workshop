package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntity;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntity;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdatableBlockEntity extends AbstractBlockEntity implements IBlockEntity {

    public UpdatableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void sendBlockUpdates() {
        var level = getLevel();
        if (level != null) {
            var state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }
}
