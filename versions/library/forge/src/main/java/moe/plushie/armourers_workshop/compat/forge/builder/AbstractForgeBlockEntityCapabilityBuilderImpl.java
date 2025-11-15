package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.neoforge.capabilities.Capabilities;

import manifold.ext.rt.api.auto;

@Available("[1.21, 1.22)")
public class AbstractForgeBlockEntityCapabilityBuilderImpl {

    public static final auto BLOCK_ITEM_HANDLER = Capabilities.ItemHandler.BLOCK;
    public static final auto BLOCK_FLUID_HANDLER = Capabilities.FluidHandler.BLOCK;
    public static final auto BLOCK_ENERGY_HANDLER = Capabilities.EnergyStorage.BLOCK;
}
