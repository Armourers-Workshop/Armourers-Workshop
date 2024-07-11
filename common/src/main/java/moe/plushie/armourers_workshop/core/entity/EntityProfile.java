package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class EntityProfile {

    private final IResourceLocation registryName;
    private final Map<SkinSlotType, Function<SkinSlotType, Integer>> supports;
    private final Collection<IEntityTypeProvider<?>> entities;
    private final boolean locked;

    public EntityProfile(IResourceLocation registryName, Map<SkinSlotType, Function<SkinSlotType, Integer>> supports, Collection<IEntityTypeProvider<?>> entities, boolean locked) {
        this.registryName = registryName;
        this.supports = supports;
        this.entities = entities;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isSupported(SkinSlotType slotType) {
        return supports.containsKey(slotType);
    }

    public int getMaxCount(SkinSlotType slotType) {
        var provider = supports.get(slotType);
        if (provider != null) {
            return provider.apply(slotType);
        }
        if (slotType == SkinSlotType.DEFAULT) {
            return slotType.getMaxSize();
        }
        return 0;
    }

    public Collection<SkinSlotType> getSlots() {
        return supports.keySet();
    }

    public Collection<IEntityTypeProvider<?>> getEntities() {
        return entities;
    }

    public IResourceLocation getRegistryName() {
        return registryName;
    }
}
