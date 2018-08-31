package riskyken.armourers_workshop.common.skin.cubes;

import net.minecraft.block.Block;
import riskyken.armourers_workshop.common.blocks.ModBlocks;

public class CubeGlassGlowing extends CubeGlass {

    @Override
    public boolean isGlowing() {
        return true;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return ModBlocks.colourableGlassGlowing;
    }
}
