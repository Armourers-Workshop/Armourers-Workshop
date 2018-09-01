package moe.plushie.armourers_workshop.common.skin.cubes;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import net.minecraft.block.Block;

public class CubeGlass extends Cube {

    @Override
    public boolean needsPostRender() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return ModBlocks.colourableGlass;
    }
}
