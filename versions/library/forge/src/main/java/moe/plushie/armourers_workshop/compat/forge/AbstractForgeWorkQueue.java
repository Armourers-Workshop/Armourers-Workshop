package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;

@Available("[1.21, 1.26)")
public class AbstractForgeWorkQueue {

    public static BlockableEventLoop<?> get(LogicalSide side) {
        return LogicalSidedProvider.WORKQUEUE.get(side);
    }
}
