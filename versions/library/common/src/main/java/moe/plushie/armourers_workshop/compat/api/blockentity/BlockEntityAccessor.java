package moe.plushie.armourers_workshop.compat.api.blockentity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Available("[16, )")
public interface BlockEntityAccessor {

    default BlockEntity abi$self() {
        return (BlockEntity) this;
    }

    default BlockPos aw2$getBlockPos() {
        return abi$self().getBlockPos();
    }

    default BlockState aw2$getBlockState() {
        return abi$self().getBlockState();
    }
}
