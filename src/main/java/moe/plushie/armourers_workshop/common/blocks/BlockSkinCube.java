package moe.plushie.armourers_workshop.common.blocks;

import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkinCube extends AbstractModBlockContainer implements IPantableBlock {
    
    public static final PropertyInteger STATE_MARKER = PropertyInteger.create("marked_face", 0, 6);
    
    public BlockSkinCube(String name, boolean glowing) {
        super(name);
        if (glowing) {
            setLightLevel(1.0F);
        }
        setHardness(1.0F);
        setLightOpacity(0);
        setSortPriority(123);
        if (glowing) {
            setSortPriority(122);
        }
        setDefaultState(this.blockState.getBaseState().withProperty(STATE_MARKER, 0));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {STATE_MARKER});
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STATE_MARKER, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE_MARKER);
    }
    
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityColourable();
    }
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setColour(colour, facing.ordinal());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setColour(rgb, facing.ordinal());
            return true;
        }
        return false;
    }

    @Override
    public int getColour(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour(facing.ordinal());
        }
        return 0;
    }
    
    @Override
    public ICubeColour getColour(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour();
        }
        return new CubeColour();
    }
    
    @Override
    public void setPaintType(IBlockAccess world, BlockPos pos, PaintType paintType, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setPaintType(paintType, facing.ordinal());
        }
    }
    
    @Override
    public PaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getPaintType(facing.ordinal());
        }
        return PaintType.NORMAL;
    }
    
    @Override
    public boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return false;
    }
}
