package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEventBus;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Optional;
import java.util.function.Function;

@Available("[1.21, )")
public class AbstractForgeBlockEntityCapabilityBuilder<T> extends AbstractBlockEntityCapabilityBuilder<T> {

    public AbstractForgeBlockEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        super(type, factory);
    }

    @Override
    public IBlockEntityCapability<T> build(OpenResourceLocation registryName) {
        if (registryName.equals(ModConstants.key("item"))) {
            return Objects.unsafeCast(createSidedCapability(AbstractForgeBlockEntityCapabilityBuilderImpl.BLOCK_ITEM_HANDLER));
        }
        if (registryName.equals(ModConstants.key("fluid"))) {
            return Objects.unsafeCast(createSidedCapability(AbstractForgeBlockEntityCapabilityBuilderImpl.BLOCK_FLUID_HANDLER));
        }
        if (registryName.equals(ModConstants.key("energy"))) {
            return Objects.unsafeCast(createSidedCapability(AbstractForgeBlockEntityCapabilityBuilderImpl.BLOCK_ENERGY_HANDLER));
        }
        throw new AssertionError();
    }

    private static <T> IBlockEntityCapability<T> createSidedCapability(BlockCapability<T, Direction> capability) {
        // register into skinnable block
        IBlockEntityCapability<T> capability1 = (level, blockPos, blockState, blockEntity, dir) -> capability.getCapability(level, blockPos, blockState, blockEntity, AbstractDirection.unwrap(dir));
        AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
            event.registerBlockEntity(capability, ModBlockEntityTypes.SKINNABLE.get().get(), (entity, context) -> {
                return entity.getCapability(capability1, AbstractDirection.wrap(context));
            });
        });
        return capability1;
    }
}
