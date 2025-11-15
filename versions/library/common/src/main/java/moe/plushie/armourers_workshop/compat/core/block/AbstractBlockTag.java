package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface AbstractBlockTag extends ITagKey<Block> {

    boolean test(BlockState blockState);
}
