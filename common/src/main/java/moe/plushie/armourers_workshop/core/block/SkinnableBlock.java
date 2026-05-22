package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractLootContextParams;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenEntitySpawnReason;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkinnableBlock extends AbstractAttachedHorizontalBlock implements AbstractBlockEntityProvider {

    public static final Property<Boolean> LIT = BlockStateProperties.LIT;
    public static final Property<Boolean> OCCUPIED = BlockStateProperties.OCCUPIED;

    public static final Property<BedPart> PART = BlockStateProperties.BED_PART;

    public SkinnableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(LIT, false)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, false));
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.SKINNABLE.get().create(level, blockPos, blockState);
    }

    @Override
    protected void abi$setPlacedBy(BlockState blockState, Level level, BlockPos blockPos, @Nullable LivingEntity entity, ItemStack itemStack) {
        var context = SkinBlockPlaceContext.of(blockPos);
        if (context == null) {
            return;
        }
        // add all part into level
        context.parts().forEach(part -> {
            var target = blockPos.offset(part.offset());
            level.setBlock(target, blockState, 11);
            var blockEntity = getBlockEntity(level, target);
            if (blockEntity != null) {
                var serializer = new TagSerializer(SerializationContext.from(blockEntity));
                part.serialize(serializer);
                blockEntity.readAdditionalData(serializer);
                blockEntity.updateBlockStates();
            }
        });
        super.abi$setPlacedBy(blockState, level, blockPos, entity, itemStack);
    }

    @Override
    protected void abi$onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        // update the block state also calls `onRemove`.
        if (!blockState.is(blockState2.getBlock())) {
            this.brokenByAnything(level, blockPos, blockState, null);
        }
        super.abi$onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    protected List<ItemStack> abi$getDrops(BlockState blockState, BlockPos blockPos, BlockGetter blockGetter, ILootBuilder context) {
        var results = super.abi$getDrops(blockState, blockPos, blockGetter, context);
        var blockEntity = context.getOptionalParameter(AbstractLootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof SkinnableBlockEntity blockEntity1) || results.isEmpty()) {
            return results;
        }
        var fixedResults = new ArrayList<ItemStack>(results.size());
        for (var itemStack : results) {
            // we will add an invalid skin item from loot table at data pack,
            // so we need fix the skin info in the drop event.
            if (itemStack.is(ModItems.SKIN.get()) && SkinDescriptor.of(itemStack).isEmpty()) {
                // when not found any dropped stack,
                // we must be remove invalid skin item.
                itemStack = blockEntity1.getDropped();
                if (itemStack == null) {
                    continue;
                }
            }
            fixedResults.add(itemStack);
        }
        return fixedResults;
    }

    @Override
    protected BlockState abi$playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        this.brokenByAnything(level, blockPos, blockState, player);
        return super.abi$playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity == null) {
            return OpenInteractionResult.FAIL;
        }
        if (blockEntity.isLinked()) {
            var result = blockEntity.getLinkedValueFromParent((level1, pos) -> {
                var state = level1.getBlockState(pos);
                return super.abi$useWithoutItem(state, level1, pos, player, interactionHand, blockHitResult);
            });
            return result.orElse(OpenInteractionResult.FAIL);
        }
        if (blockEntity.isBed() && !player.isSecondaryUseActive()) {
            if (ModPermissions.SKINNABLE_SLEEP.accept(blockEntity, player)) {
                var bedState = Blocks.RED_BED.defaultBlockState().setValue(PART, BedPart.HEAD);
                return super.abi$useWithoutItem(bedState, level, blockEntity.getBedPos(), player, interactionHand, blockHitResult);
            }
        }
        if (blockEntity.isSeat() && !player.isSecondaryUseActive()) {
            if (ModPermissions.SKINNABLE_SIT.accept(blockEntity, player)) {
                if (level.isClientSide()) {
                    return OpenInteractionResult.CONSUME;
                }
                var seatPos = blockEntity.getSeatPos().adding(0.5f, 0.5f, 0.5f);
                var seatEntity = getSeatEntity((ServerLevel) level, blockEntity.getParentPos(), seatPos);
                if (seatEntity == null) {
                    return OpenInteractionResult.FAIL; // it is using
                }
                player.startRiding(seatEntity);
                return OpenInteractionResult.SUCCESS;
            }
        }
        if (blockEntity.isInventory()) {
            var result = player.openMenu(ModMenuTypes.SKINNABLE, level, blockPos);
            if (result.consumesAction()) {
                player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            }
            return result;
        }
        return OpenInteractionResult.FAIL;
    }

    @Override
    protected BlockState abi$getStateForPlacement(BlockPlaceContext context) {
        var state = super.abi$getStateForPlacement(context);
        if (state != null && context instanceof SkinBlockPlaceContext context1) {
            if (context1.getProperty(SkinProperty.BLOCK_GLOWING)) {
                state = state.setValue(LIT, true);
            }
        }
        return state;
    }

    @Override
    protected ItemStack abi$getCloneItemStack(BlockState blockState, LevelReader blockGetter, BlockPos blockPos, boolean includeData) {
        var blockEntity = getParentBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getSkin().asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean abi$isBed(BlockGetter level, BlockPos blockPos, BlockState blockState, @Nullable Entity player) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.isBed();
        }
        return false;
    }

    @Override
    protected boolean abi$isLadder(BlockGetter level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.isLadder();
        }
        return false;
    }

    @Override
    protected void abi$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT, PART, OCCUPIED);
    }

    @Override
    protected boolean abi$hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int abi$getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection dir) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getAnalogOutputSignal(dir);
        }
        return 0;
    }

    @Override
    protected boolean abi$isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int abi$getSignal(BlockState state, BlockGetter level, BlockPos blockPos, OpenDirection direction) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getSignal(direction);
        }
        return 0;
    }

    @Override
    protected int abi$getDirectSignal(BlockState state, BlockGetter level, BlockPos blockPos, OpenDirection direction) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getDirectSignal(direction);
        }
        return 0;
    }

    @Override
    protected VoxelShape abi$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        var blockEntity = getBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getShape();
        }
        return Shapes.empty();
    }

    @Override
    protected VoxelShape abi$getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        var blockEntity = getBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getCollisionShape();
        }
        return Shapes.empty();
    }

    public void forEach(Level level, BlockPos pos, Consumer<BlockPos> consumer) {
        var blockEntity = getParentBlockEntity(level, pos);
        if (blockEntity == null) {
            return;
        }
        var parentPos = blockEntity.getBlockPos();
        for (var offset : blockEntity.getRefers()) {
            var targetPos = parentPos.offset(offset);
            if (!targetPos.equals(pos)) {
                consumer.accept(targetPos);
            }
        }
    }

    public void brokenByAnything(Level level, BlockPos blockPos, BlockState blockState, @Nullable Player player) {
        if (dropItems(level, blockPos, player)) {
            if (level instanceof ServerLevel serverLevel) {
                killSeatEntities(serverLevel, blockPos);
            }
            forEach(level, blockPos, target -> level.setBlock(target, Blocks.AIR.defaultBlockState(), 35));
        }
    }

    protected void killSeatEntities(ServerLevel level, BlockPos blockPos) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            var seatPos = blockEntity.getSeatPos().adding(0.5f, 0.5f, 0.5f);
            killSeatEntity(level, blockEntity.getParentPos(), seatPos);
        }
    }

    protected void killSeatEntity(ServerLevel level, BlockPos blockPos, OpenVector3d pos) {
        var searchRect = new AABB(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 1, pos.z() + 1);
        for (var entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                entity.kill(level);
            }
        }
    }

    public boolean dropItems(Level level, BlockPos blockPos, @Nullable Player player) {
        var blockEntity = getBlockEntity(level, blockPos);
        var parentBlockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity == null || parentBlockEntity == null || parentBlockEntity.isDropped()) {
            return false;
        }
        // anyway, we only drop all items once.
        var droppedStack = parentBlockEntity.getSkin().asItemStack();
        blockEntity.setDropped(droppedStack); // mark the attacked block
        parentBlockEntity.setDropped(droppedStack);
        if (parentBlockEntity.isInventory()) {
            DataSerializers.dropContents(level, blockPos, parentBlockEntity);
        }
        return true;
    }

    private SkinnableBlockEntity getBlockEntity(BlockGetter level, BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SkinnableBlockEntity blockEntity1) {
            return blockEntity1;
        }
        return null;
    }

    private SkinnableBlockEntity getParentBlockEntity(BlockGetter level, BlockPos blockPos) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getParent();
        }
        return null;
    }

    @Nullable
    private SeatEntity getSeatEntity(ServerLevel level, BlockPos blockPos, OpenVector3d pos) {
        var searchRect = new AABB(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 1, pos.z() + 1);
        for (var entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                if (entity.getPassengers().isEmpty()) {
                    return entity;
                }
                return null; // is using
            }
        }
        var entity = ModEntityTypes.SEAT.get().create(level, BlockPos.ZERO, null, OpenEntitySpawnReason.SPAWN_ITEM_USE);
        entity.setPos(pos.x(), pos.y(), pos.z());
        entity.setBlockPos(blockPos);
        level.addFreshEntity(entity);
        return entity;
    }
}
