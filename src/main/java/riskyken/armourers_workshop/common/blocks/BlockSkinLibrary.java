package riskyken.armourers_workshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntitySkinLibrary;

public class BlockSkinLibrary extends AbstractModBlockContainer {

    public BlockSkinLibrary() {
        super(LibBlockNames.ARMOUR_LIBRARY);
        setSortPriority(198);
    }
    /*
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.ARMOUR_LIBRARY, world, x, y, z);
        }
        return true;
    }
    */
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinLibrary();
    }
}
