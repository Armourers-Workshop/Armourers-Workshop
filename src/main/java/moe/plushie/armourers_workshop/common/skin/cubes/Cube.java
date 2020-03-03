package moe.plushie.armourers_workshop.common.skin.cubes;

import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import net.minecraft.block.Block;

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
        return ModBlocks.SKIN_CUBE;
    }
}
