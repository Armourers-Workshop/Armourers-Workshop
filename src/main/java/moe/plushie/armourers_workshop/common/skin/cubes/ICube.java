package moe.plushie.armourers_workshop.common.skin.cubes;

import net.minecraft.block.Block;

public interface ICube {
    
    /** Will this cube glow in the dark? */
    public boolean isGlowing();
    
    /** Should this cube be rendered after the world? */
    public boolean needsPostRender();
    
    /** Get the cubes ID */
    public byte getId();
    
    public Block getMinecraftBlock();
}
