package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import net.minecraft.world.level.block.Block;

public interface ISkinGeometryType extends IRegistryEntry {

    /**
     * Get the geometry ID
     */
    int id();

    /**
     * Get the geometry binding minecraft block.
     */
    Block block();
}
