package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.network.BlockEntityDataPacket;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdatableBlockEntity extends AbstractBlockEntity {

    public UpdatableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void sendBlockUpdates() {
        var level = getLevel();
        if (level != null) {
            var state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    @Override
    protected BlockEntityDataPacket abi$getUpdateDataPacket() {
        return new BlockEntityDataPacket(this);
    }
}
