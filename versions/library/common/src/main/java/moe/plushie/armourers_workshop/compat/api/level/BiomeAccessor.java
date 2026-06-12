package moe.plushie.armourers_workshop.compat.api.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface BiomeAccessor {

    Level getLevel();

    BlockPos getBlockPos();
}
