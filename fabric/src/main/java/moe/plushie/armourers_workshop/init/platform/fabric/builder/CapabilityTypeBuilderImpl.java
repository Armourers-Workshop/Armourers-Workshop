package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.other.ICapabilityType;
import moe.plushie.armourers_workshop.api.other.builder.ICapabilityTypeBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.init.capability.CapabilityStorage;
import net.minecraft.resources.ResourceLocation;
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
    public IRegistryObject<ICapabilityType<T>> build(String name) {
        ResourceLocation registryName = ArmourersWorkshop.getResource(name);
        ICapabilityType<T> capabilityType = new ICapabilityType<T>() {
            @Override
            public Optional<T> get(Entity entity) {
                return CapabilityStorage.getCapability(entity, this);
            }
        };
        CapabilityStorage.registerCapability(registryName, capabilityType, factory);
        return new IRegistryObject<ICapabilityType<T>>() {
            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public ICapabilityType<T> get() {
                return capabilityType;
            }
        };
    }
}
