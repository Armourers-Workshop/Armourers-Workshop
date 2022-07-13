package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldUpdater {

    private static final WorldUpdater INSTANCE = new WorldUpdater();

    private final HashMap<RegistryKey<World>, ArrayList<IWorldUpdateTask>> allTasks = new HashMap<>();

    public static WorldUpdater getInstance() {
        return INSTANCE;
    }

    public synchronized void submit(IWorldUpdateTask task) {
        allTasks.computeIfAbsent(task.getLevel().dimension(), k -> new ArrayList<>()).add(task);
    }

    public void tick(World world) {
        RegistryKey<World> key = world.dimension();
        if (isEmpty(key)) {
            return;
        }
        TileEntityUpdateCombiner.begin();
        ArrayList<IWorldUpdateTask> failedTasks = new ArrayList<>();
        for (int count = ModConfig.Common.blockTaskRate; count > 0; /* noop */) {
            IWorldUpdateTask task = poll(key);
            if (task == null) {
                break; // no more tasks to run
            }
            ActionResultType resultType = task.run(world);
            if (resultType.consumesAction()) {
                count -= 1;
            } else if (resultType == ActionResultType.FAIL) {
                failedTasks.add(task);
            }
        }
        failedTasks.forEach(this::submit);
        TileEntityUpdateCombiner.end();
    }

    public synchronized boolean isEmpty(RegistryKey<World> key) {
        ArrayList<IWorldUpdateTask> m = allTasks.get(key);
        return m == null || m.isEmpty();
    }

    @Nullable
    public synchronized IWorldUpdateTask poll(RegistryKey<World> key) {
        ArrayList<IWorldUpdateTask> m = allTasks.get(key);
        if (m != null && m.size() != 0) {
            return m.remove(0);
        }
        return null;
    }
}
