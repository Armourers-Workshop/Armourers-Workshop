package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.ICapabilityTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCapabilityManager;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class CapabilityTypeBuilderImpl<T> implements ICapabilityTypeBuilder<T> {

    Class<T> type;
    Function<Entity, Optional<T>> factory;

    public CapabilityTypeBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public IRegistryKey<ICapabilityType<T>> build(String name) {
        return AbstractForgeCapabilityManager.register(name, type, factory);
    }
}
