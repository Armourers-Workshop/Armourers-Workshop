package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.world.World;

public interface IWorldColourable {
    
    public boolean setColour(World world, int x, int y, int z, int colour);
    
    public int getColour(World world, int x, int y, int z);
}
