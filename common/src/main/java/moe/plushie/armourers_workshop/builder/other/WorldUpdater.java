package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class WorldUpdater {

    private static final WorldUpdater INSTANCE = new WorldUpdater();

    private final ArrayList<IWorldUpdateTask> failedTasks = new ArrayList<>();
    private final HashMap<ResourceKey<Level>, AutoMergeQueue> allTasks = new HashMap<>();

    public static WorldUpdater getInstance() {
        return INSTANCE;
    }

    public synchronized void submit(IWorldUpdateTask task) {
        allTasks.computeIfAbsent(task.getLevel().dimension(), k -> new AutoMergeQueue()).push(task);
    }

    public void tick(Level level) {
        ResourceKey<Level> key = level.dimension();
        if (isEmpty(key)) {
            return;
        }
        BlockUtils.beginCombiner();
//        long startAt = System.currentTimeMillis();
        for (int count = ModConfig.Common.blockTaskRate; count > 0; /* noop */) {
            IWorldUpdateTask task = poll(key);
            if (task == null) {
                break; // no more tasks to run
            }
            InteractionResult resultType = task.run(level);
            if (resultType.consumesAction()) {
                count -= 1;
            } else if (resultType == InteractionResult.FAIL) {
                failedTasks.add(task);
            }
        }
        if (!failedTasks.isEmpty()) {
            failedTasks.forEach(this::submit);
            failedTasks.clear();
        }
        BlockUtils.endCombiner();
    }

    public synchronized void drain(Level level) {
        AutoMergeQueue queue = allTasks.remove(level.dimension());
        if (queue == null || queue.isEmpty()) {
            return;
        }
        BlockUtils.beginCombiner();
        while (!queue.isEmpty()) {
            queue.pop().run(level);
        }
        BlockUtils.endCombiner();
    }

    public synchronized boolean isEmpty(ResourceKey<Level> key) {
        AutoMergeQueue m = allTasks.get(key);
        return m == null || m.isEmpty();
    }

    @Nullable
    public synchronized IWorldUpdateTask poll(ResourceKey<Level> key) {
        AutoMergeQueue m = allTasks.get(key);
        if (m != null && !m.isEmpty()) {
            return m.pop();
        }
        return null;
    }

    public static class AutoMergeQueue {

        private final HashMap<BlockPos, IWorldUpdateTask> fastTable = new HashMap<>();
        private final ArrayList<IWorldUpdateTask> tasks = new ArrayList<>();

        public void push(IWorldUpdateTask task) {
            BlockPos blockPos = task.getBlockPos();
            IWorldUpdateTask pendingTask = fastTable.get(blockPos);
            if (pendingTask == null) {
                tasks.add(task);
                fastTable.put(blockPos, task);
                return;
            }
            if (pendingTask instanceof AutoMergeTask) {
                ((AutoMergeTask) pendingTask).add(task);
                return;
            }
            AutoMergeTask mergedTask = new AutoMergeTask(pendingTask);
            mergedTask.add(task);
            fastTable.put(blockPos, mergedTask);
        }

        public IWorldUpdateTask pop() {
            IWorldUpdateTask task = tasks.remove(0);
            IWorldUpdateTask mergedTask = fastTable.remove(task.getBlockPos());
            if (mergedTask != null) {
                return mergedTask;
            }
            return task;
        }

        public boolean isEmpty() {
            return tasks.isEmpty();
        }
    }

    public static class AutoMergeTask implements IWorldUpdateTask {

        private final Level level;
        private final BlockPos blockPos;
        private final ArrayList<IWorldUpdateTask> tasks = new ArrayList<>();

        public AutoMergeTask(IWorldUpdateTask task) {
            this.level = task.getLevel();
            this.blockPos = task.getBlockPos();
            this.tasks.add(task);
        }

        public void add(IWorldUpdateTask task) {
            this.tasks.add(task);
        }

        public void optimize(Level level) {
            ArrayList<IWorldUpdateTask> optimizedTasks = new ArrayList<>();
            BlockState lastBlockState = null;
            for (IWorldUpdateTask task : tasks) {
                BlockState newBlockState = task.getBlockState();
                if (!Objects.equals(newBlockState, lastBlockState)) {
                    lastBlockState = newBlockState;
                    optimizedTasks.clear();
                }
                optimizedTasks.add(task);
            }
            if (tasks.size() != optimizedTasks.size()) {
                tasks.clear();
                tasks.addAll(optimizedTasks);
            }
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public BlockPos getBlockPos() {
            return blockPos;
        }

        @Override
        public BlockState getBlockState() {
            return null;
        }

        @Override
        public InteractionResult run(Level level) {
            // before the run, we will optimize the block changes.
            optimize(level);
            int changes = 0;
            ArrayList<IWorldUpdateTask> failedTasks = new ArrayList<>();
            for (IWorldUpdateTask task : tasks) {
                InteractionResult resultType = task.run(level);
                if (resultType.consumesAction()) {
                    changes += 1;
                }
                if (resultType == InteractionResult.FAIL) {
                    failedTasks.add(task);
                }
            }
            if (!failedTasks.isEmpty()) {
                tasks.clear();
                tasks.addAll(failedTasks);
                return InteractionResult.FAIL;
            }
            if (changes != 0) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }
}
