package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class SkinCubeBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider, IBlockTintColorProvider {

    // a better solution is use `OptionalDirectionProperty` as marker flags,
    // but some third-party mods will rotate this block, such as world edit's.
    // they usually to handle `DirectionProperty` for the compatible vanilla,
    // so we use vanilla API to avoid problems
    public static final DirectionProperty MARKER = DirectionProperty.create("marker", Direction.values());
    public static final BooleanProperty HAS_MARKER = BooleanProperty.create("has_marker");

    public SkinCubeBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(HAS_MARKER, false).setValue(MARKER, Direction.NORTH));
    }

    public static OptionalDirection getMarker(BlockState blockState) {
        if (blockState.getOptionalValue(HAS_MARKER).orElse(false)) {
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
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.SKIN_CUBE.get().create(level, blockPos, blockState);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState state1, Direction dir) {
        // the same block can be omitted
        return state.getBlock() == state1.getBlock();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MARKER);
        builder.add(HAS_MARKER);
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
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }

    @Override
    public int getTintColor(BlockState blockState, @Nullable BlockGetter reader, @Nullable BlockPos blockPos, int index) {
        if (reader == null || blockPos == null) {
            return 0xffffffff;
        }
        Direction direction = Direction.NORTH;
        if (index > 0 && index < 7) {
            direction = Direction.values()[index - 1];
        }
        BlockEntity tileEntity = reader.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintColor paintColor = ((IPaintable) tileEntity).getColor(direction);
            return paintColor.getRGB() | 0xff000000;
        }
        return 0xffffffff;
    }
}
