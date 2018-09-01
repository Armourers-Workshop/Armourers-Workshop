package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import net.minecraft.world.IBlockAccess;

public interface IPantableBlock {
    
    /** @deprecated Replaced by {@link #setColour(IBlockAccess world, int x, int y, int z, byte[] rgb, int side)} */
    @Deprecated
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour, int side);
    
    public boolean setColour(IBlockAccess world, int x, int y, int z, byte[] rgb, int side);
    
    public int getColour(IBlockAccess world, int x, int y, int z, int side);
    
    public void setPaintType(IBlockAccess world, int x, int y, int z, PaintType paintType, int side);
    
    public PaintType getPaintType(IBlockAccess world, int x, int y, int z, int side);
    
    public ICubeColour getColour(IBlockAccess world, int x, int y, int z);
    
    public boolean isRemoteOnly(IBlockAccess world, int x, int y, int z, int side);
}
