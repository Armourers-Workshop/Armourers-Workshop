package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.tileentity.OutfitMakerTileEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class OutfitMakerBlock extends AbstractHorizontalBlock {

    public OutfitMakerBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if (ModContainerTypes.open(ModContainerTypes.OUTFIT_MAKER, player, world, pos)) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new OutfitMakerTileEntity();
    }
}
