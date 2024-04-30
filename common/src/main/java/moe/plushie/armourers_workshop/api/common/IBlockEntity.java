package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntity {

    void setChanged();

    void sendBlockUpdates();

    void readAdditionalData(IDataSerializer serializer);

    void writeAdditionalData(IDataSerializer serializer);

    boolean isRemoved();

    Level getLevel();

    BlockPos getBlockPos();

    BlockState getBlockState();
}
