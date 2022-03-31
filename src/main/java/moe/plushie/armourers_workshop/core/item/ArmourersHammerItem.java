package moe.plushie.armourers_workshop.core.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ArmourersHammerItem extends FlavouredItem {

    public ArmourersHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack itemStack, ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockState newState = state.rotate(world, pos, Rotation.CLOCKWISE_90);
        if (!newState.equals(state)) {
            world.setBlock(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }
}
