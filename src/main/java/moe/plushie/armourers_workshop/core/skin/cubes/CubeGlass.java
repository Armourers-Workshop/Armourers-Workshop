package moe.plushie.armourers_workshop.core.skin.cubes;

import net.minecraft.block.Block;

public class CubeGlass extends Cube {

    @Override
    public boolean needsPostRender() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {

//        return ModBlocks.SKIN_CUBE_GLASS/;
        return null;
    }
}
