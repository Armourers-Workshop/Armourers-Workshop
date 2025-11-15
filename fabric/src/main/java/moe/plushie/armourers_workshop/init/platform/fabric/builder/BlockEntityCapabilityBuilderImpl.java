package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.fabric.builder.AbstractFabricBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class BlockEntityCapabilityBuilderImpl<T> implements IBlockEntityCapabilityBuilder<T> {

    private final AbstractFabricBlockEntityCapabilityBuilder<T> builder;

    public BlockEntityCapabilityBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.builder = new AbstractFabricBlockEntityCapabilityBuilder<>(type, factory);
    }

    @Override
    public IRegistryHolder<IBlockEntityCapability<T>> build(String name) {
        return Registries.BLOCK_ENTITY_CAPABILITIES.register(name, builder::build);
    }
}
