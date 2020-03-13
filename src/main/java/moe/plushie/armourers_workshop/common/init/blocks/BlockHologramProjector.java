package moe.plushie.armourers_workshop.common.init.blocks;

import java.util.Random;

import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHologramProjector extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = PropertyDirection.create("facing");
    
    public BlockHologramProjector() {
        super(LibBlockNames.HOLOGRAM_PROJECTOR);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATE_FACING, EnumFacing.NORTH));
        setSortPriority(120);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE_FACING });
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        return this.getDefaultState().withProperty(STATE_FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE_FACING).ordinal();
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        BlockUtils.dropInventoryBlocks(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return true;
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        int dir = BlockUtils.determineOrientation(pos, placer);
        EnumFacing enumfacing = EnumFacing.byIndex(dir);
        return getDefaultState().withProperty(STATE_FACING, enumfacing);
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        updatePoweredState(worldIn, pos);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        openGui(playerIn, EnumGuiId.HOLOGRAM_PROJECTOR, worldIn, pos, state, facing);
        return true;
    }
    
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(world, pos, neighbor);
        updatePoweredState(world, pos);
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        updatePoweredState(worldIn, pos);
    }
    
    private void updatePoweredState(IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityHologramProjector) {
            ((TileEntityHologramProjector)tileEntity).updatePoweredState();
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHologramProjector();
    }
}
