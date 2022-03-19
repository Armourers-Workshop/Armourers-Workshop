package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.function.Function;

public class EntityProfile {

    private final HashMap<ISkinType, Function<ISkinType, Integer>> supports;
    private final boolean isFixed;

    public EntityProfile(HashMap<ISkinType, Function<ISkinType, Integer>> supports, boolean isFixed) {
        this.supports = supports;
        this.isFixed = isFixed;
    }

    public boolean canCustomize() {
        return !isFixed;
    }

    public boolean canSupport(ISkinType type) {
        return supports.containsKey(type);
    }

    public boolean isDynamicOverrideArmor(Entity entity) {
        return !(entity instanceof PlayerEntity);
    }

    public int getMaxCount(ISkinType type) {
        Function<ISkinType, Integer> provider = supports.get(type);
        if (provider != null) {
            return provider.apply(type);
        }
        return 0;
    }


}
