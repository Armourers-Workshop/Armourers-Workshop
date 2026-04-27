package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IDeltaTracker;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Available("[21, )")
public class AbstractDeltaTracker implements IDeltaTracker {

    private final boolean isPaused;

    private final DeltaTracker delta;
    private final TickRateManager rateManager;

    private AbstractDeltaTracker(Level level, DeltaTracker delta, boolean isPaused) {
        this.delta = delta;
        this.rateManager = Objects.flatMap(level, Level::tickRateManager);
        this.isPaused = isPaused;
    }

    public static AbstractDeltaTracker of(DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        return new AbstractDeltaTracker(minecraft.level, deltaTracker, minecraft.isPaused());
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean isFrozen() {
        return rateManager != null && !rateManager.runsNormally();
    }

    @Override
    public float partialTick() {
        return delta.getGameTimeDeltaPartialTick(true);
    }

    @Override
    public float partialTick(Entity entity) {
        if (rateManager == null) {
            return partialTick();
        }
        boolean flag = rateManager.isEntityFrozen(entity);
        return delta.getGameTimeDeltaPartialTick(flag);
    }

    @Override
    public float rate() {
        if (isPaused) {
            return 0.0f;
        }
        if (rateManager == null) {
            return 0.0f;
        }
        if (rateManager.runsNormally()) {
            return rateManager.tickrate() / 20.0f;
        }
        return 0.0f;
    }
}
