package riskyken.armourersWorkshop.common.skin.cubes;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

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
