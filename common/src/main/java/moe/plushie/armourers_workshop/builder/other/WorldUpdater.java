package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldUpdater {

    private static final WorldUpdater INSTANCE = new WorldUpdater();

    private final HashMap<ResourceKey<Level>, ArrayList<IWorldUpdateTask>> allTasks = new HashMap<>();

    public static WorldUpdater getInstance() {
        return INSTANCE;
    }

    public synchronized void submit(IWorldUpdateTask task) {
        allTasks.computeIfAbsent(task.getLevel().dimension(), k -> new ArrayList<>()).add(task);
    }

    public void tick(Level world) {
        ResourceKey<Level> key = world.dimension();
        if (isEmpty(key)) {
            return;
        }
        BlockUtils.beginCombiner();
        ArrayList<IWorldUpdateTask> failedTasks = new ArrayList<>();
        for (int count = ModConfig.Common.blockTaskRate; count > 0; /* noop */) {
            IWorldUpdateTask task = poll(key);
            if (task == null) {
                break; // no more tasks to run
            }
            InteractionResult resultType = task.run(world);
            if (resultType.consumesAction()) {
                count -= 1;
            } else if (resultType == InteractionResult.FAIL) {
                failedTasks.add(task);
            }
        }
        failedTasks.forEach(this::submit);
        BlockUtils.endCombiner();
    }

    public synchronized boolean isEmpty(ResourceKey<Level> key) {
        ArrayList<IWorldUpdateTask> m = allTasks.get(key);
        return m == null || m.isEmpty();
    }

    @Nullable
    public synchronized IWorldUpdateTask poll(ResourceKey<Level> key) {
        ArrayList<IWorldUpdateTask> m = allTasks.get(key);
        if (m != null && m.size() != 0) {
            return m.remove(0);
        }
        return null;
    }
}
