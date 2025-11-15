package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractBlockEntityCapabilityBuilder<T> {

    protected final Class<T> type;
    protected final Function<Entity, Optional<T>> factory;

    public AbstractBlockEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.type = type;
        this.factory = factory;
    }

    public abstract IBlockEntityCapability<T> build(OpenResourceLocation registryName);
}
