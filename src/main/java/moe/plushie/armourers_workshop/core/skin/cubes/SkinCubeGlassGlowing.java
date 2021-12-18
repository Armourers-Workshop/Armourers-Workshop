package moe.plushie.armourers_workshop.core.skin.cubes;

import net.minecraft.block.Block;

public class SkinCubeGlassGlowing extends SkinCubeGlass {

    @Override
    public boolean isGlowing() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return null;
        //return ModBlocks.SKIN_CUBE_GLASS_GLOWING;
    }
}
