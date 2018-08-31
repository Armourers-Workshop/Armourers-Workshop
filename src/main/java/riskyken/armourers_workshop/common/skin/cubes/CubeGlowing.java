package riskyken.armourers_workshop.common.skin.cubes;

import net.minecraft.block.Block;
import riskyken.armourers_workshop.common.blocks.ModBlocks;

public class CubeGlowing extends Cube {

    @Override
    public boolean isGlowing() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return ModBlocks.colourableGlowing;
    }
}
