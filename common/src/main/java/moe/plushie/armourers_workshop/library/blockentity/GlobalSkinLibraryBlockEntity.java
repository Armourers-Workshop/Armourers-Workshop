package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GlobalSkinLibraryBlockEntity extends UpdatableBlockEntity {

    public GlobalSkinLibraryBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
    }
}
