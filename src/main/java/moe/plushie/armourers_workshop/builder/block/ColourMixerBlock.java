package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.tileentity.ColourMixerTileEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class ColourMixerBlock extends AbstractHorizontalBlock {

    public ColourMixerBlock(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public static IBlockColor getColorProvider(int tintIndex) {  // by ColorHandlerEvent.Block.register(ColourMixerBlock.getColorProvider(1), ...)
        return (state, world, pos, tintIndex1) -> {
            if (world == null || pos == null || tintIndex != tintIndex1) {
                return 0xffffffff;
            }
            TileEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ColourMixerTileEntity) {
                return ((ColourMixerTileEntity) entity).getColor().getRGB() | 0xff000000;
            }
            return 0xffffffff;
        };
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ColourMixerTileEntity();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        for(Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
            } else {
                return this.defaultBlockState().setValue(FACING, direction.getOpposite());
            }
        }
        return null;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        ModContainerTypes.open(ModContainerTypes.COLOUR_MIXER, player, IWorldPosCallable.create(world, pos));
        return ActionResultType.CONSUME;
    }

}
