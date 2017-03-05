package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.world.IBlockAccess;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.painting.PaintType;

public interface IPantableBlock {
    
    @Deprecated
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour, int side);
    
    public boolean setColour(IBlockAccess world, int x, int y, int z, byte[] rgb, int side);
    
    public int getColour(IBlockAccess world, int x, int y, int z, int side);
    
    public void setPaintType(IBlockAccess world, int x, int y, int z, PaintType paintType, int side);
    
    public PaintType getPaintType(IBlockAccess world, int x, int y, int z, int side);
    
    public ICubeColour getColour(IBlockAccess world, int x, int y, int z);
    
    public boolean isRemoteOnly(IBlockAccess world, int x, int y, int z, int side);
}
