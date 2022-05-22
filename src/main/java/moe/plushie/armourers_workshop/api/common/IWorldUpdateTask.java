package moe.plushie.armourers_workshop.api.common;

import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

public interface IWorldUpdateTask {

    World getLevel();

    ActionResultType run(World world);
}
