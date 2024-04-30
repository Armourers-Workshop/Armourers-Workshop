package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.registry.ICapabilityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCapabilityManager;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class CapabilityTypeBuilderImpl<T> implements ICapabilityTypeBuilder<T> {

    private final Class<T> type;
    private final Function<Entity, Optional<T>> factory;

    public CapabilityTypeBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public IRegistryKey<ICapabilityType<T>> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        ModLog.debug("Registering Capability Type '{}'", registryName);
        return AbstractForgeCapabilityManager.register(registryName, type, factory);
    }
}
