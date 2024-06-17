package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class WorldUpdater {

    private static final WorldUpdater INSTANCE = new WorldUpdater();

    private final LinkedList<IWorldUpdateTask> failedTasks = new LinkedList<>();
    private final HashMap<ResourceKey<Level>, AutoMergeQueue> allTasks = new HashMap<>();

    public static WorldUpdater getInstance() {
        return INSTANCE;
    }

    public synchronized void submit(IWorldUpdateTask task) {
        allTasks.computeIfAbsent(task.getLevel().dimension(), k -> new AutoMergeQueue()).push(task);
    }

    public void tick(Level level) {
        var key = level.dimension();
        if (isEmpty(key)) {
            return;
        }
        BlockUtils.performBatch(() -> {
            // long startAt = System.currentTimeMillis();
            for (int count = ModConfig.Common.blockTaskRate; count > 0; /* noop */) {
                var task = poll(key);
                if (task == null) {
                    break; // no more tasks to run
                }
                var resultType = task.run(level);
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
        });
    }

    public synchronized void drain(Level level) {
        var queue = allTasks.remove(level.dimension());
        if (queue == null || queue.isEmpty()) {
            return;
        }
        BlockUtils.performBatch(() -> {
            while (!queue.isEmpty()) {
                queue.pop().run(level);
            }
        });
    }

    public synchronized boolean isEmpty(ResourceKey<Level> key) {
        var m = allTasks.get(key);
        return m == null || m.isEmpty();
    }

    @Nullable
    public synchronized IWorldUpdateTask poll(ResourceKey<Level> key) {
        var m = allTasks.get(key);
        if (m != null && !m.isEmpty()) {
            return m.pop();
        }
        return null;
    }

    public static class AutoMergeQueue {

        private final HashMap<BlockPos, IWorldUpdateTask> fastTable = new HashMap<>();
        private final ArrayList<IWorldUpdateTask> tasks = new ArrayList<>();

        public void push(IWorldUpdateTask task) {
            var blockPos = task.getBlockPos();
            var pendingTask = fastTable.get(blockPos);
            if (pendingTask == null) {
                tasks.add(task);
                fastTable.put(blockPos, task);
                return;
            }
            if (pendingTask instanceof AutoMergeTask mergeTask) {
                mergeTask.add(task);
                return;
            }
            var mergedTask = new AutoMergeTask(pendingTask);
            mergedTask.add(task);
            fastTable.put(blockPos, mergedTask);
        }

        public IWorldUpdateTask pop() {
            var task = tasks.remove(0);
            var mergedTask = fastTable.remove(task.getBlockPos());
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
            var optimizedTasks = new ArrayList<IWorldUpdateTask>();
            BlockState lastBlockState = null;
            for (var task : tasks) {
                var newBlockState = task.getBlockState();
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
            var changes = 0;
            var failedTasks = new ArrayList<IWorldUpdateTask>();
            for (var task : tasks) {
                var resultType = task.run(level);
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
