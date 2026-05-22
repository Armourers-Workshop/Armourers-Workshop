package moe.plushie.armourers_workshop.core.skin.particle.runtime;

import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface LevelAccessor {

    Block getBlock(OpenVector3f position);

    List<OpenVoxelShape> getCollisionBlocks(OpenAxisAlignedBoundingBox box);

    boolean isLoaded(OpenVector3f position);
}
