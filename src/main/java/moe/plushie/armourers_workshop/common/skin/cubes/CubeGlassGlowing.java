package moe.plushie.armourers_workshop.common.skin.cubes;

import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import net.minecraft.block.Block;

public class CubeGlassGlowing extends CubeGlass {

    @Override
    public boolean isGlowing() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return ModBlocks.SKIN_CUBE_GLASS_GLOWING;
    }
}
