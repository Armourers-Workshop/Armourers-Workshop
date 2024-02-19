package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockSnapshot {

    BlockState getState();

    CompoundTag getTag();
}
