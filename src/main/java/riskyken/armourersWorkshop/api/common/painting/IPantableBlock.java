package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.world.IBlockAccess;
import riskyken.armourersWorkshop.common.skin.cubes.ICubeColour;

public interface IPantableBlock {
    
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour);
    
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour, int side);
    
    public int getColour(IBlockAccess world, int x, int y, int z, int side);
    
    public ICubeColour getColour(IBlockAccess world, int x, int y, int z);
}
