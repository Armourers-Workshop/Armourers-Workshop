package moe.plushie.armourers_workshop.api.common;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface IEntityDataBuilder {

    <T> void define(EntityDataAccessor<T> accessor, T object);
}
