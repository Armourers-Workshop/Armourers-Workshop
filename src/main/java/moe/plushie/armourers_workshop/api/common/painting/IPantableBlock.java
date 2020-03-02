package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IPantableBlock {
    
    /** @deprecated Replaced by {@link #setColour(IBlockAccess world, int x, int y, int z, byte[] rgb, int side)} */
    @Deprecated
    public boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing facing);
    
    public boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing facing);
    
    public int getColour(IBlockAccess world, BlockPos pos, EnumFacing facing);
    
    public void setPaintType(IBlockAccess world, BlockPos pos, IPaintType paintType, EnumFacing facing);
    
    public IPaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing facing);
    
    public ICubeColour getColour(IBlockAccess world, BlockPos pos);
    
    public boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing facing);
}
