package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.world.IBlockAccess;

public interface IPantableBlock {
    
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour);
    
    public int getColour(IBlockAccess world, int x, int y, int z);
}
