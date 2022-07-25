package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

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
