package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import net.minecraft.world.level.block.Block;

public interface ISkinCubeType extends IRegistryEntry {

    /**
     * Get the cubes ID
     */
    int getId();

    Block getBlock();

    /**
     * Should this cube be rendered after the world?
     */
    boolean isGlass();

    /**
     * Will this cube glow in the dark?
     */
    boolean isGlowing();
}
