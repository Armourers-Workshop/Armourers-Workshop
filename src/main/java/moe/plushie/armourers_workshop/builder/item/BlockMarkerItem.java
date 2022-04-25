package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.utils.OptionalDirection;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMarkerItem extends FlavouredItem {

    public BlockMarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack itemStack, ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasProperty(SkinCubeBlock.MARKER)) {
            OptionalDirection direction = OptionalDirection.of(context.getClickedFace());
            if (direction.equals(blockState.getValue(SkinCubeBlock.MARKER))) {
                direction = OptionalDirection.NONE;
            }
            world.setBlock(blockPos, blockState.setValue(SkinCubeBlock.MARKER, direction), 3);
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }
}