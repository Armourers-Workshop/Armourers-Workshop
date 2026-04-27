package moe.plushie.armourers_workshop.compat.core.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityDataBuilder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

@Available("[21, )")
public class AbstractEntityDataBuilder implements IEntityDataBuilder {

    private final SynchedEntityData.Builder impl;

    private AbstractEntityDataBuilder(SynchedEntityData.Builder impl) {
        this.impl = impl;
    }

    public static IEntityDataBuilder wrap(SynchedEntityData.Builder builder) {
        return new AbstractEntityDataBuilder(builder);
    }

    public static SynchedEntityData.Builder unwrap(IEntityDataBuilder builder) {
        return ((AbstractEntityDataBuilder) builder).impl;
    }

    @Override
    public <T> void define(EntityDataAccessor<T> accessor, T object) {
        impl.define(accessor, object);
    }
}
