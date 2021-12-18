package moe.plushie.armourers_workshop.core.api.common.skin;

import net.minecraft.block.Block;

public interface ICube {

    /**
     * Should this cube be rendered after the world?
     */
    boolean isGlass();

    /**
     * Will this cube glow in the dark?
     */
    boolean isGlowing();

    /**
     * Get the cubes ID
     */
    byte getId();

    Block getMinecraftBlock();
}
