package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCapability;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class AbstractForgeEntityCapabilityBuilder<T> extends AbstractEntityCapabilityBuilder<T> {

    public AbstractForgeEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        super(type, factory);
    }

    @Override
    public IEntityCapability<T> build(OpenResourceKey registryName) {
        return new AbstractForgeCapability<>(registryName, type, factory);
    }
}
