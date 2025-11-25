package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class ArmourersHammerItem extends FlavouredItem {

    public ArmourersHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$useOnFirst(ItemStack itemStack, IUseOnContext context) {
        var level = context.level();
        var pos = context.clickedPos();
        var state = level.getBlockState(pos);
        var newState = state.rotate(Rotation.CLOCKWISE_90);
        if (!newState.equals(state)) {
            level.setBlock(pos, newState, Constants.BlockFlags.BLOCK_UPDATE);
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }
}
