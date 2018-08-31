package riskyken.armourers_workshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntityHologramProjector;

public class BlockHologramProjector extends AbstractModBlockContainer {

    public BlockHologramProjector() {
        super(LibBlockNames.HOLOGRAM_PROJECTOR);
        setSortPriority(150);
    }
    /*
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int dir = BlockUtils.determineOrientation(x, y, z, entityLivingBase);
        world.setBlockMetadataWithNotify(x, y, z, dir, 2);
    }
    
    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int p_149714_5_) {
        updatePoweredState(world, x, y, z);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityHologramProjector();
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.HOLOGRAM_PROJECTOR, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, EnumFacing axis) {
        world.setBlockMetadataWithNotify(x, y, z, axis.ordinal(), 2);
        return true;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        updatePoweredState(world, x, y, z);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        updatePoweredState(world, x, y, z);
    }
    */
    private void updatePoweredState(World world, BlockPos pos) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && tileEntity instanceof TileEntityHologramProjector) {
                ((TileEntityHologramProjector)tileEntity).updatePoweredState();
            }
        }
    }
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        // TODO Auto-generated method stub
        return new TileEntityHologramProjector();
    }
}
