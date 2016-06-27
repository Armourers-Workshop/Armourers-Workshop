package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.painting.PaintType;

public interface IPantableBlock {
    
    @Deprecated
    public boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing side);
    
    public boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing side);
    
    public int getColour(IBlockAccess world, BlockPos pos, EnumFacing side);
    
    public void setPaintType(IBlockAccess world, BlockPos pos, PaintType paintType, EnumFacing side);
    
    public PaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing side);
    
    public ICubeColour getColour(IBlockAccess world, BlockPos pos);
    
    public boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing side);
}
