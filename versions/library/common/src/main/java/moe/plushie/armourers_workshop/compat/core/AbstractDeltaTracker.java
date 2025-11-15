package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Available("[1.21, )")
public class AbstractDeltaTracker {

    private final Level level;
    private final DeltaTracker delta;
    private final boolean isPaused;

    public AbstractDeltaTracker(Level level, DeltaTracker delta, boolean isPaused) {
        this.level = level;
        this.delta = delta;
        this.isPaused = isPaused;
    }

    public float getPartialTick() {
        return delta.getGameTimeDeltaPartialTick(true);
    }

    public float getPartialTick(Entity entity) {
        var tickRateManager = getTickRateManager();
        if (tickRateManager == null) {
            return getPartialTick();
        }
        boolean flag = tickRateManager.isEntityFrozen(entity);
        return delta.getGameTimeDeltaPartialTick(flag);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isFrozen() {
        var tickRateManager = getTickRateManager();
        return tickRateManager != null && !tickRateManager.runsNormally();
    }

    public TickRateManager getTickRateManager() {
        if (level != null) {
            return level.tickRateManager();
        }
        return null;
    }
}
