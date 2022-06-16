package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.tileentity.SkinCubeTileEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.utils.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class SkinCubeBlock extends AbstractHorizontalBlock implements IBlockTintColorProvider {

    // a better solution is use `OptionalDirectionProperty` as marker flags,
    // but some third-party mods will rotate this block, such as world edit's.
    // they usually to handle `DirectionProperty` for the compatible vanilla,
    // so we use vanilla API to avoid problems
    public static final DirectionProperty MARKER = DirectionProperty.create("marker", Direction.values());
    public static final BooleanProperty HAS_MARKER = BooleanProperty.create("has_marker");

    public SkinCubeBlock(AbstractBlock.Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(HAS_MARKER, false).setValue(MARKER, Direction.NORTH));
    }

    public static OptionalDirection getMarker(BlockState blockState) {
        if (blockState.getValue(HAS_MARKER)) {
            return OptionalDirection.of(blockState.getValue(MARKER));
        }
        return OptionalDirection.NONE;
    }

    public static BlockState setMarker(BlockState blockState, OptionalDirection direction) {
        Direction dir = direction.getDirection();
        if (dir != null) {
            return blockState.setValue(HAS_MARKER, true).setValue(MARKER, dir);
        }
        return blockState.setValue(HAS_MARKER, false);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState state1, Direction dir) {
        // the same block can be omitted
        return state.getBlock() == state1.getBlock();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SkinCubeTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MARKER);
        builder.add(HAS_MARKER);
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        Direction facing = rotation.rotate(blockState.getValue(FACING));
        Direction marker = rotation.rotate(blockState.getValue(MARKER));
        return blockState.setValue(FACING, facing).setValue(MARKER, marker);
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return rotate(blockState, mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack itemStack = super.getPickBlock(state, target, world, pos, player);
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        IPaintColor paintColor = null;
        BlockRayTraceResult traceResult = (BlockRayTraceResult)target;
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof IPaintable) {
            paintColor = ((IPaintable) tileEntity).getColor(traceResult.getDirection());
        }
        if (paintColor != null) {
            ColorUtils.setColor(itemStack, paintColor);
        }
        return itemStack;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getTintColor(BlockState blockState, @Nullable IBlockDisplayReader reader, @Nullable BlockPos blockPos, int index) {
        if (reader == null || blockPos == null) {
            return 0xffffffff;
        }
        Direction direction = Direction.NORTH;
        if (index > 0 && index < 7) {
            direction = Direction.values()[index - 1];
        }
        TileEntity tileEntity = reader.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintColor paintColor = ((IPaintable) tileEntity).getColor(direction);
            return paintColor.getRGB() | 0xff000000;
        }
        return 0xffffffff;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable IBlockReader world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        tooltips.addAll(TranslateUtils.subtitles(getDescriptionId() + ".flavour"));
    }
}
