package riskyken.armourersWorkshop.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn,
            BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState blockState = worldIn.getBlockState(pos);
        if (CubeRegistry.INSTANCE.isBuildingBlock(blockState.getBlock())) {
            if (!worldIn.isRemote) {
                int meta = world.getBlockMetadata(blockLocation);
                int newMeta = side + 1;
                if (newMeta == meta) {
                    //This side is already marked.
                    world.setBlockMetaData(blockLocation, 0, 2);
                } else {
                    world.setBlockMetaData(blockLocation, newMeta, 2);
                }
            }
            return EnumActionResult.PASS;
        }
        return EnumActionResult.FAIL;
    }
}
