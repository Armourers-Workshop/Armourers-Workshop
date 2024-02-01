package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class EntityProfile {

    private final ResourceLocation registryName;
    private final Map<SkinSlotType, Function<SkinSlotType, Integer>> supports;
    private final Collection<IEntityTypeProvider<?>> entities;
    private final boolean locked;

    public EntityProfile(ResourceLocation registryName, Map<SkinSlotType, Function<SkinSlotType, Integer>> supports, Collection<IEntityTypeProvider<?>> entities, boolean locked) {
        this.registryName = registryName;
        this.supports = supports;
        this.entities = entities;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getMaxCount(SkinSlotType slotType) {
        auto provider = supports.get(slotType);
        if (provider != null) {
            return provider.apply(slotType);
        }
        return 0;
    }

    public Collection<SkinSlotType> getSlots() {
        return supports.keySet();
    }

    public Collection<IEntityTypeProvider<?>> getEntities() {
        return entities;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
