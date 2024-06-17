package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Rotation;

public class ArmourersHammerItem extends FlavouredItem implements IItemHandler {

    public ArmourersHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOnFirst(ItemStack itemStack, UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        var newState = state.rotate(Rotation.CLOCKWISE_90);
        if (!newState.equals(state)) {
            level.setBlock(pos, newState, Constants.BlockFlags.BLOCK_UPDATE);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
