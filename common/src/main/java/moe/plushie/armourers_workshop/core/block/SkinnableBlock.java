package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.IBlockEntityProvider;
import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.init.ModEntities;
import moe.plushie.armourers_workshop.init.ModMenus;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SkinnableBlock extends AbstractAttachedHorizontalBlock implements IBlockEntityProvider, IBlockHandler {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public SkinnableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(LIT, false)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return ModBlockEntities.SKINNABLE_CUBE.get().create();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        SkinBlockPlaceContext context = SkinBlockPlaceContext.of(blockPos);
        if (context == null) {
            return;
        }
        // add all part into level
        context.getParts().forEach(p -> {
            BlockPos target = blockPos.offset(p.getOffset());
            level.setBlock(target, blockState, 11);
            SkinnableBlockEntity tileEntity = getTileEntity(level, target);
            if (tileEntity != null) {
                tileEntity.readFromNBT(p.getEntityTag());
                tileEntity.updateBlockStates();
            }
        });
        super.setPlacedBy(level, blockPos, blockState, entity, itemStack);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        // update the block state also calls `onRemove`.
        if (!blockState.is(blockState2.getBlock())) {
            this.brokenByAnything(level, blockPos, blockState, null);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        this.brokenByAnything(level, blockPos, blockState, player);
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult traceResult) {
        SkinnableBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity == null) {
            return InteractionResult.FAIL;
        }
        if (tileEntity.isLinked()) {
            BlockPos linkedPos = tileEntity.getLinkedBlockPos();
            BlockState linkedState = level.getBlockState(linkedPos);
            return linkedState.getBlock().use(linkedState, level, linkedPos, player, hand, traceResult);
        }
        if (tileEntity.isBed() && !player.isShiftKeyDown()) {
            if (ModPermissions.SKINNABLE_SLEEP.accept(tileEntity, player)) {
                return Blocks.RED_BED.use(blockState, level, tileEntity.getBedPos(), player, hand, traceResult);
            }
        }
        if (tileEntity.isSeat() && !player.isShiftKeyDown()) {
            if (ModPermissions.SKINNABLE_SIT.accept(tileEntity, player)) {
                if (level.isClientSide) {
                    return InteractionResult.CONSUME;
                }
                Vector3d seatPos = tileEntity.getSeatPos().add(0.5f, 0.5f, 0.5f);
                SeatEntity seatEntity = getSeatEntity(level, tileEntity.getParentPos(), seatPos);
                if (seatEntity == null) {
                    return InteractionResult.FAIL; // it is using
                }
                player.startRiding(seatEntity, true);
                return InteractionResult.SUCCESS;
            }
        }
        if (tileEntity.isInventory()) {
            if (MenuManager.openMenu(ModMenus.SKINNABLE, player, level, blockPos)) {
                player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && context instanceof SkinBlockPlaceContext) {
            SkinBlockPlaceContext context1 = (SkinBlockPlaceContext) context;
            if (context1.getProperty(SkinProperty.BLOCK_GLOWING)) {
                state = state.setValue(LIT, true);
            }
        }
        return state;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        SkinnableBlockEntity tileEntity = getParentTileEntity(blockGetter, blockPos);
        if (tileEntity != null) {
            return tileEntity.getDescriptor().asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isCustomBed(BlockGetter level, BlockPos blockPos, BlockState blockState, @Nullable Entity player) {
        SkinnableBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            return tileEntity.isBed();
        }
        return false;
    }

    @Override
    public boolean isCustomLadder(BlockGetter level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        SkinnableBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            return tileEntity.isLadder();
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT, PART, OCCUPIED);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        SkinnableBlockEntity tileEntity = getTileEntity(blockGetter, blockPos);
        if (tileEntity != null) {
            return tileEntity.getShape();
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        SkinnableBlockEntity tileEntity = getTileEntity(blockGetter, blockPos);
        if (tileEntity != null && tileEntity.noCollision()) {
            return Shapes.empty();
        }
        return super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    public void forEach(Level level, BlockPos pos, Consumer<BlockPos> consumer) {
        SkinnableBlockEntity tileEntity = getParentTileEntity(level, pos);
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

    public void brokenByAnything(Level level, BlockPos blockPos, BlockState blockState, @Nullable Player player) {
        if (dropItems(level, blockPos, player)) {
            killSeatEntities(level, blockPos);
            forEach(level, blockPos, target -> level.setBlock(target, Blocks.AIR.defaultBlockState(), 35));
        }
    }

    public void killSeatEntities(Level level, BlockPos blockPos) {
        SkinnableBlockEntity tileEntity = getParentTileEntity(level, blockPos);
        if (tileEntity != null) {
            Vector3d seatPos = tileEntity.getSeatPos().add(0.5f, 0.5f, 0.5f);
            killSeatEntity(level, tileEntity.getParentPos(), seatPos);
        }
    }

    public boolean dropItems(Level level, BlockPos blockPos, @Nullable Player player) {
        SkinnableBlockEntity tileEntity = getParentTileEntity(level, blockPos);
        if (tileEntity == null || tileEntity.isDropped()) {
            return false;
        }
        // anyway, we only drop all items once.
        tileEntity.setDropped(true);
        if (player == null || !player.abilities.instabuild) {
            DataSerializers.dropItemStack(level, blockPos, tileEntity.getDescriptor().asItemStack());
        }
        if (tileEntity.isInventory()) {
            DataSerializers.dropContents(level, blockPos, tileEntity);
        }
        return true;
    }

    private SkinnableBlockEntity getTileEntity(BlockGetter level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof SkinnableBlockEntity) {
            return (SkinnableBlockEntity) tileEntity;
        }
        return null;
    }

    private SkinnableBlockEntity getParentTileEntity(BlockGetter level, BlockPos blockPos) {
        SkinnableBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            return tileEntity.getParent();
        }
        return null;
    }

    @Nullable
    private SeatEntity getSeatEntity(Level level, BlockPos blockPos, Vector3d pos) {
        AABB searchRect = AABB.ofSize(1, 1, 1).move(pos.x(), pos.y(), pos.z());
        for (SeatEntity entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                if (entity.getPassengers().isEmpty()) {
                    return entity;
                }
                return null; // is using
            }
        }
        SeatEntity entity = new SeatEntity(ModEntities.SEAT.get(), level);
        entity.setPos(pos.x(), pos.y(), pos.z());
        entity.setBlockPos(blockPos);
        level.addFreshEntity(entity);
        return entity;
    }

    private void killSeatEntity(Level level, BlockPos blockPos, Vector3d pos) {
        AABB searchRect = AABB.ofSize(1, 1, 1).move(pos.x(), pos.y(), pos.z());
        for (SeatEntity entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                entity.kill();
            }
        }
    }
}
