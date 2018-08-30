package riskyken.armourersWorkshop.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;

public class ItemBlockMarker extends AbstractModItem {

    public ItemBlockMarker() {
        super(LibItemNames.BLOCK_MARKER);
        setSortPriority(12);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (CubeRegistry.INSTANCE.isBuildingBlock(state.getBlock())) {
            if (!worldIn.isRemote) {
                //state.getBlock().getMetaFromState(state)
                /*
                int meta = world.getBlockMetadata(x, y, z);
                int newMeta = side + 1;
                if (newMeta == meta) {
                    //This side is already marked.
                    world.setBlockMetadataWithNotify(x, y, z, 0, 2);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                }
                */
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
