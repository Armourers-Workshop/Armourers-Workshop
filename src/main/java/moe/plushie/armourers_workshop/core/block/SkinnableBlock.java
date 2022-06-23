package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.permission.PermissionManager;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.init.common.ModEntities;
import moe.plushie.armourers_workshop.utils.SkinItemUseContext;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
public class SkinnableBlock extends HorizontalFaceBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public SkinnableBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(LIT, false)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, false));
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
            SkinnableTileEntity tileEntity = getTileEntity(world, target);
            if (tileEntity != null) {
                tileEntity.readFromNBT(p.getEntityTag());
                tileEntity.updateBlockStates();
            }
        });
        super.setPlacedBy(world, pos, state, entity, itemStack);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // remove all part from world
        dropItems(world, pos, player);
        forEach(world, pos, target -> {
            world.setBlock(target, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, target, Block.getId(state));
        });
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity == null) {
            return ActionResultType.FAIL;
        }
        if (tileEntity.isLinked()) {
            BlockPos linkedPos = tileEntity.getLinkedBlockPos();
            BlockState linkedState = world.getBlockState(linkedPos);
            return linkedState.getBlock().use(linkedState, world, linkedPos, player, hand, traceResult);
        }
        if (tileEntity.isBed() && !player.isShiftKeyDown()) {
            if (PermissionManager.shouldSleep(tileEntity, player)) {
                return Blocks.RED_BED.use(state, world, tileEntity.getBedPos(), player, hand, traceResult);
            }
        }
        if (tileEntity.isSeat() && !player.isShiftKeyDown()) {
            if (PermissionManager.shouldSit(tileEntity, player)) {
                if (world.isClientSide) {
                    return ActionResultType.CONSUME;
                }
                Vector3d seatPos = tileEntity.getSeatPos().add(0.5f, 0.5f, 0.5f);
                SeatEntity seatEntity = getSeatEntity(world, tileEntity.getParentPos(), seatPos);
                if (seatEntity == null) {
                    return ActionResultType.FAIL; // it is using
                }
                player.startRiding(seatEntity, true);
                return ActionResultType.SUCCESS;
            }
        }
        if (tileEntity.isInventory()) {
            if (ModContainerTypes.open(ModContainerTypes.SKINNABLE, player, world, pos)) {
                player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
                return ActionResultType.sidedSuccess(world.isClientSide);
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        // remove all part from world
        dropItems(world, pos, null);
        forEach(world, pos, target -> super.onBlockExploded(state, world, target, explosion));
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && context instanceof SkinItemUseContext) {
            SkinItemUseContext context1 = (SkinItemUseContext) context;
            if (context1.getProperty(SkinProperty.BLOCK_GLOWING)) {
                state = state.setValue(LIT, true);
            }
        }
        return state;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        SkinnableTileEntity tileEntity = getParentTileEntity(world, pos);
        if (tileEntity != null) {
            return tileEntity.getDescriptor().asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            return tileEntity.isBed();
        }
        return false;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            return tileEntity.isLadder();
        }
        return super.isLadder(state, world, pos, entity);
    }


    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT, PART, OCCUPIED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            return tileEntity.getShape();
        }
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null && tileEntity.noCollision()) {
            return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    public void forEach(World world, BlockPos pos, Consumer<BlockPos> consumer) {
        SkinnableTileEntity tileEntity = getParentTileEntity(world, pos);
        if (tileEntity == null) {
            return;
        }
        BlockPos parentPos = tileEntity.getBlockPos();
        for (BlockPos offset : tileEntity.getRefers()) {
            BlockPos targetPos = parentPos.offset(offset);
            if (!targetPos.equals(pos)) {
                consumer.accept(targetPos);
            }
        }
    }

    public void dropItems(World world, BlockPos pos, @Nullable PlayerEntity player) {
        SkinnableTileEntity tileEntity = getParentTileEntity(world, pos);
        if (tileEntity == null) {
            return;
        }
        if (player == null || !player.abilities.instabuild) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tileEntity.getDescriptor().asItemStack());
        }
        if (tileEntity.isInventory()) {
            InventoryHelper.dropContents(world, pos, tileEntity);
        }
    }

    private SkinnableTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof SkinnableTileEntity) {
            return (SkinnableTileEntity) tileEntity;
        }
        return null;
    }

    private SkinnableTileEntity getParentTileEntity(IBlockReader world, BlockPos pos) {
        SkinnableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            return tileEntity.getParent();
        }
        return null;
    }

    @Nullable
    private SeatEntity getSeatEntity(World world, BlockPos blockPos, Vector3d pos) {
        AxisAlignedBB searchRect = AxisAlignedBB.ofSize(1, 1, 1).move(pos);
        for (SeatEntity entity : world.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                if (entity.getPassengers().isEmpty()) {
                    return entity;
                }
                return null;// is using
            }
        }
        SeatEntity entity = new SeatEntity(ModEntities.SEAT, world);
        entity.setPos(pos.x(), pos.y(), pos.z());
        entity.setBlockPos(blockPos);
        world.addFreshEntity(entity);
        return entity;
    }
}
