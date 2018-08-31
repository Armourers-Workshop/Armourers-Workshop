package riskyken.armourers_workshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntitySkinningTable;

public class BlockSkinningTable extends AbstractModBlockContainer {

    public BlockSkinningTable() {
        super(LibBlockNames.SKINNING_TABLE);
        setSortPriority(150);
    }
    /*
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinningTable) {
            TileEntitySkinningTable tileEntity = (TileEntitySkinningTable) te;
            BlockUtils.dropInventoryBlocks(world, tileEntity.getCraftingInventory(), x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.SKNNING_TABLE, world, x, y, z);
        }
        return true;
    }
*/
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinningTable();
    }
}
