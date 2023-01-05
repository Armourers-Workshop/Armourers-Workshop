package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class EntityProfile {

    private final ResourceLocation registryName;
    private final HashMap<ISkinType, Function<ISkinType, Integer>> supports;
    private final Collection<IEntityType<?>> entities;
    private final boolean locked;

    public EntityProfile(ResourceLocation registryName, HashMap<ISkinType, Function<ISkinType, Integer>> supports, Collection<IEntityType<?>> entities, boolean locked) {
        this.registryName = registryName;
        this.supports = supports;
        this.entities = entities;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
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

    public Collection<IEntityType<?>> getEntities() {
        return entities;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
