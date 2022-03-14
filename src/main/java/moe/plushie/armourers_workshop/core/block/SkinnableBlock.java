package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.core.utils.SkinItemUseContext;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SkinnableBlock extends HorizontalFaceBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SkinnableBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(LIT, false));
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        SkinItemUseContext context = SkinItemUseContext.of(pos);
        if (context == null) {
            return;
        }
        // add all part into world
        context.getParts().forEach(p -> {
            BlockPos target = pos.offset(p.getOffset());
            world.setBlock(target, state, 11);
            TileEntity tileEntity1 = world.getBlockEntity(target);
            if (tileEntity1 instanceof SkinnableTileEntity) {
                SkinnableTileEntity tileEntity2 = (SkinnableTileEntity) tileEntity1;
                tileEntity2.setRefer(p.getOffset());
                tileEntity2.setShape(p.getShape());
                if (target.equals(pos)) {
                    tileEntity2.setRefers(context.getBlockPosList());
                    tileEntity2.setDescriptor(context.getSkin());
                }
                tileEntity2.updateBlockStates();
            }
        });
        super.setPlacedBy(world, pos, state, entity, itemStack);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // remove all part from world
        forEach(world, pos, target -> {
            world.setBlock(target, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, target, Block.getId(state));
        });
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        // remove all part from world
        forEach(world, pos, target -> super.onBlockExploded(state, world, target, explosion));
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SkinnableTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof SkinnableTileEntity) {
            return ((SkinnableTileEntity) entity).getShape();
        }
        return VoxelShapes.empty();
    }

    public void forEach(World world, BlockPos pos, Consumer<BlockPos> consumer) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof SkinnableTileEntity) {
            tileEntity = ((SkinnableTileEntity) tileEntity).getParent();
        }
        if (tileEntity instanceof SkinnableTileEntity) {
            BlockPos parentPos = tileEntity.getBlockPos();
            for (BlockPos offset : ((SkinnableTileEntity) tileEntity).getRefers()) {
                BlockPos targetPos = parentPos.offset(offset);
                if (!targetPos.equals(pos)) {
                    consumer.accept(targetPos);
                }
            }
        }
    }
}
