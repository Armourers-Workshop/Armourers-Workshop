package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockMarker extends AbstractModItem {

    public ItemBlockMarker() {
        super(LibItemNames.BLOCK_MARKER);
        setCreativeTab(ArmourersWorkshop.TAB_PAINTING_TOOLS);
        setSortPriority(12);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (CubeRegistry.INSTANCE.isBuildingBlock(state.getBlock())) {
            if (!worldIn.isRemote) {
                int meta = state.getBlock().getMetaFromState(state);
                int newMeta = facing.ordinal() + 1;
                IBlockState newState = null;
                if (newMeta == meta) {
                    // This side is already marked.
                    newState = state.getBlock().getStateFromMeta(0);
                } else {
                    newState = state.getBlock().getStateFromMeta(newMeta);
                }
                worldIn.setBlockState(pos, newState, 2);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
