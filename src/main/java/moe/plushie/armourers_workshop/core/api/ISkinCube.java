package moe.plushie.armourers_workshop.core.api;

import net.minecraft.block.Block;

public interface ISkinCube {

    /**
     * Get the cubes ID
     */
    int getId();

    /**
     * Should this cube be rendered after the world?
     */
    boolean isGlass();

    /**
     * Will this cube glow in the dark?
     */
    boolean isGlowing();

    Block getMinecraftBlock();

    String getRegistryName();
}
