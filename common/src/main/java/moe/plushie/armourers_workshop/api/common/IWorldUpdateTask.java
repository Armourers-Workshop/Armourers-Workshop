package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public interface IWorldUpdateTask {

    Level getLevel();

    InteractionResult run(Level level);
}
