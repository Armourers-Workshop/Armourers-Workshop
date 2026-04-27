package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractEntityCapabilityBuilder<T> {

    protected final Class<T> type;
    protected final Function<Entity, Optional<T>> factory;

    public AbstractEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.type = type;
        this.factory = factory;
    }

    public abstract IEntityCapability<T> build(OpenResourceKey registryName);
}
