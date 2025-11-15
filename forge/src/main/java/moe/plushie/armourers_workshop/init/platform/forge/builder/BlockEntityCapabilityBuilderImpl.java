package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class BlockEntityCapabilityBuilderImpl<T> implements IBlockEntityCapabilityBuilder<T> {

    private final AbstractForgeBlockEntityCapabilityBuilder<T> builder;

    public BlockEntityCapabilityBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.builder = new AbstractForgeBlockEntityCapabilityBuilder<T>(type, factory);
    }

    @Override
    public IRegistryHolder<IBlockEntityCapability<T>> build(String name) {
        return Registries.BLOCK_ENTITY_CAPABILITIES.register(name, builder::build);
    }
}
