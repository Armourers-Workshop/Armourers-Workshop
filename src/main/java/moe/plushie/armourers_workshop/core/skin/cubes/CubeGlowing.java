package moe.plushie.armourers_workshop.core.skin.cubes;

import net.minecraft.block.Block;

public class CubeGlowing extends Cube {

    @Override
    public boolean isGlowing() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return null;
        //return ModBlocks.SKIN_CUBE_GLOWING;
    }
}
