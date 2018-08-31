package riskyken.armourers_workshop.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntityColourMixer;

public class BlockColourMixer extends AbstractModBlockContainer {

    public BlockColourMixer() {
        super(LibBlockNames.COLOUR_MIXER);
        setSortPriority(124);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // TODO Auto-generated method stub
        return EnumBlockRenderType.MODEL;
    }
    
    /*
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.COLOUR_MIXER, world, x, y, z);
        }
        return true;
    }
*/
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityColourMixer();
    }
}
