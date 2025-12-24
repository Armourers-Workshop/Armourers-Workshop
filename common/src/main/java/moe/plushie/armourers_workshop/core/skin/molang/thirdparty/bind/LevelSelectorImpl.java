package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;
import net.minecraft.world.level.Level;

public class LevelSelectorImpl<T extends Level> implements LevelSelector {

    protected T level;

    public LevelSelectorImpl<T> apply(T level) {
        this.level = level;
        return this;
    }

    public T level() {
        return level;
    }

    @Override
    public int moonPhase() {
        return level.getMoonPhase();
    }

    @Override
    public double days() {
        // ((float) (level.getDayTime() + 6000L) / 24000) % 1;
        return (level.getDayTime() + 6000L) / 24000d;
    }

    @Override
    public double timestamp() {
        return level.getDayTime();
    }

    @Override
    public int weather() {
        if (level.isThundering()) {
            return 2;
        }
        if (level.isRaining()) {
            return 1;
        }
        return 0; // sunny
    }

    @Override
    public String dimensionId() {
        return level.dimension().location().toString();
    }
}
