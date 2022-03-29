package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class GlobalSkinLibraryBlock extends AbstractHorizontalBlock {

    public GlobalSkinLibraryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        ModContainerTypes.open(ModContainerTypes.SKIN_LIBRARY_GLOBAL, player, IWorldPosCallable.create(world, pos));
        return ActionResultType.CONSUME;
    }
}
