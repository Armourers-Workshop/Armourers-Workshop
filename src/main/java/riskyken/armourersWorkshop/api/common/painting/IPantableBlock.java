package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.world.World;

public interface IPantableBlock {
    
    public boolean setColour(World world, int x, int y, int z, int colour);
    
    public int getColour(World world, int x, int y, int z);
}
