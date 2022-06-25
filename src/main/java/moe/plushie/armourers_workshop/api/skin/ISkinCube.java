package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public interface ISkinCube {

    /**
     * Get the cubes ID
     */
    int getId();

    ResourceLocation getRegistryName();

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