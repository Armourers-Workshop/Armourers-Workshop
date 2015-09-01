package riskyken.armourersWorkshop.common.skin.cubes;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

public class Cube implements ICube {
    
    protected final byte id;
    
    public Cube() {
        id = CubeRegistry.INSTANCE.getTotalCubes();
    }
    
    @Override
    public boolean isGlowing() {
        return false;
    }
    
    @Override
    public boolean needsPostRender() {
        return false;
    }
    
    @Override
    public byte getId() {
        return id;
    }
    
    @Override
    public Block getMinecraftBlock() {
        return ModBlocks.colourable;
    }
}
