package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.core.data.CapabilityStorage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@Available("[16, )")
public class AbstractFabricEntityCapabilityBuilder<T> extends AbstractEntityCapabilityBuilder<T> {

    public AbstractFabricEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        super(type, factory);
    }

    @Override
    public IEntityCapability<T> build(OpenResourceKey registryName) {
        var capabilityType = new IEntityCapability<T>() {
            @Override
            public Optional<T> get(Entity entity) {
                return CapabilityStorage.getCapability(entity, this);
            }
        };
        CapabilityStorage.registerCapability(registryName, capabilityType, factory);
        return capabilityType;
    }
}
