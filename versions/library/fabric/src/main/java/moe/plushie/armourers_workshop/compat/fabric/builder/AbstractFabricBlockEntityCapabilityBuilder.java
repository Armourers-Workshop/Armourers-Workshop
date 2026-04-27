package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@Available("[16, )")
@SuppressWarnings("SameParameterValue")
public class AbstractFabricBlockEntityCapabilityBuilder<T> extends AbstractBlockEntityCapabilityBuilder<T> {

    public AbstractFabricBlockEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        super(type, factory);
    }

    @Override
    public IBlockEntityCapability<T> build(OpenResourceKey registryName) {
        if (registryName.equals(ModConstants.key("item"))) {
            return Objects.unsafeCast(createSidedCapability(ItemStorage.SIDED));
        }
        if (registryName.equals(ModConstants.key("fluid"))) {
            return Objects.unsafeCast(createSidedCapability(FluidStorage.SIDED));
        }
        if (registryName.equals(ModConstants.key("energy"))) {
            var api = findStaticField("team.reborn.energy.api.EnergyStorage", "SIDED");
            if (api != null) {
                return Objects.unsafeCast(createSidedCapability(api));
            }
        }
        return null;
    }

    private static <T> IBlockEntityCapability<T> createSidedCapability(BlockApiLookup<T, Direction> lookup) {
        // register into skinnable block
        IBlockEntityCapability<T> capability1 = (level, blockPos, blockState, blockEntity, dir) -> lookup.find(level, blockPos, blockState, blockEntity, AbstractDirection.unwrap(dir));
        lookup.registerForBlockEntity((entity, context) -> entity.getCapability(capability1, AbstractDirection.wrap(context)), ModBlockEntityTypes.SKINNABLE.get().get());
        return capability1;
    }


    private static <T> BlockApiLookup<T, Direction> findStaticField(String className, String fieldName) {
        try {
            var obj = Class.forName(className);
            var field = obj.getDeclaredField(fieldName);
            return Objects.unsafeCast(field.get(obj));
        } catch (Exception e) {
            return null;
        }
    }
}
