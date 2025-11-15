package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.fabric.builder.AbstractFabricEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class EntityCapabilityBuilderImpl<T> implements IEntityCapabilityBuilder<T> {

    private final AbstractFabricEntityCapabilityBuilder<T> builder;

    public EntityCapabilityBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.builder = new AbstractFabricEntityCapabilityBuilder<>(type, factory);
    }

    @Override
    public IRegistryHolder<IEntityCapability<T>> build(String name) {
        return Registries.ENTITY_CAPABILITIES.register(name, builder::build);
    }
}
