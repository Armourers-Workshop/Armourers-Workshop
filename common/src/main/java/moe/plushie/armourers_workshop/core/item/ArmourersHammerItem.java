package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.extend.IExtendedItemHandler;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class ArmourersHammerItem extends FlavouredItem implements IExtendedItemHandler {

    public ArmourersHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOnFirst(ItemStack itemStack, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockState newState = state.rotate(Rotation.CLOCKWISE_90);
        if (!newState.equals(state)) {
            world.setBlock(pos, newState, Constants.BlockFlags.BLOCK_UPDATE);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
