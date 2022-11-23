package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public class EntityProfile {

    private final ResourceLocation registryName;
    private final HashMap<ISkinType, Function<ISkinType, Integer>> supports;
    private final boolean editable;

    public EntityProfile(ResourceLocation registryName, HashMap<ISkinType, Function<ISkinType, Integer>> supports, boolean editable) {
        this.registryName = registryName;
        this.supports = supports;
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean canSupport(ISkinType type) {
        return supports.containsKey(type);
    }

    public int getMaxCount(ISkinType type) {
        Function<ISkinType, Integer> provider = supports.get(type);
        if (provider != null) {
            return provider.apply(type);
        }
        return 0;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
