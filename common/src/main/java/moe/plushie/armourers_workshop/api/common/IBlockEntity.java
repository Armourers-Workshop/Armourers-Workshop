package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntity {

    void setChanged();

    void sendBlockUpdates();

    void readFromNBT(CompoundTag tag);

    void writeToNBT(CompoundTag tag);

    boolean isRemoved();

    Level getLevel();

    BlockPos getBlockPos();

    BlockState getBlockState();
}
