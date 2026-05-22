package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Export;
import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

@Available("[16, )")
@SuppressWarnings("NullableProblems")
public class AbstractBlock extends AbstractBlockImpl {

    public AbstractBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }


    protected void abi$updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int i, int j) {
        super.updateIndirectNeighbourShapes(blockState, levelAccessor, blockPos, i, j);
    }

    @Override
    public final void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int i, int j) {
        abi$updateIndirectNeighbourShapes(blockState, levelAccessor, blockPos, i, j);
    }

    //boolean abi$isPathfindable(BlockState blockState, PathComputationType pathComputationType);

    protected BlockState abi$updateShape(BlockState blockState, OpenDirection direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2, null);
    }

    @Override
    public final BlockState updateShape(BlockState blockState, OpenDirection direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2, Object context) {
        return abi$updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    protected boolean abi$skipRendering(BlockState blockState, BlockState blockState2, OpenDirection direction) {
        return super.skipRendering(blockState, blockState2, direction, null);
    }

    @Override
    public final boolean skipRendering(BlockState blockState, BlockState blockState2, OpenDirection direction, Object context) {
        return abi$skipRendering(blockState, blockState2, direction);
    }

    protected void abi$neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl, null);
    }

    @Override
    public final void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, Object context) {
        abi$neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    protected void abi$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public final void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        abi$onPlace(blockState, level, blockPos, blockState2, bl);
    }

    protected void abi$onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl, null);
    }

    @Override
    public final void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, Object context) {
        abi$onRemove(blockState, level, blockPos, blockState2, bl);
    }

    protected OpenInteractionResult abi$onAttack(Level level, BlockPos blockPos, BlockState blockState, OpenDirection direction, Player player, OpenInteractionHand hand) {
        return OpenInteractionResult.PASS;
    }

    @Export("called in the mixin")
    public final OpenInteractionResult attackBlock(Level level, BlockPos blockPos, BlockState blockState, OpenDirection direction, Player player, OpenInteractionHand hand) {
        return abi$onAttack(level, blockPos, blockState, direction, player, hand);
    }

    protected void abi$onExplosionHit(BlockState blockState, ServerLevel level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        super.onExplosionHit(blockState, level, blockPos, explosion, biConsumer, null);
    }

    @Override
    public final void onExplosionHit(BlockState blockState, ServerLevel level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, Object context) {
        abi$onExplosionHit(blockState, level, blockPos, explosion, biConsumer);
    }

    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return super.useWithoutItem(blockState, level, blockPos, player, interactionHand, blockHitResult, null);
    }

    @Override
    public final OpenInteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        return abi$useWithoutItem(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    protected OpenInteractionResult abi$useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult, null);
    }

    @Override
    public final OpenInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        return abi$useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    protected boolean abi$triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j) {
        return super.triggerEvent(blockState, level, blockPos, i, j);
    }

    @Override
    public final boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j) {
        return abi$triggerEvent(blockState, level, blockPos, i, j);
    }

    protected RenderShape abi$getRenderShape(BlockState blockState) {
        return super.getRenderShape(blockState);
    }

    @Override
    public final RenderShape getRenderShape(BlockState blockState) {
        return abi$getRenderShape(blockState);
    }

    protected boolean abi$isBed(BlockGetter level, BlockPos blockPos, BlockState blockState, @Nullable Entity entity) {
        return false;
    }

    @Export("called in the mixin")
    public final boolean isBed(BlockState blockState, BlockGetter level, BlockPos blockPos, LivingEntity entity) {
        return abi$isBed(level, blockPos, blockState, entity);
    }

    protected boolean abi$isLadder(BlockGetter level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        return false; // state.is(BlockTags.CLIMBABLE);
    }

    @Export("called in the mixin")
    public final boolean isLadder(BlockState blockState, LevelReader level, BlockPos blockPos, LivingEntity entity) {
        return abi$isLadder(level, blockPos, blockState, entity);
    }

    protected boolean abi$useShapeForLightOcclusion(BlockState blockState) {
        return super.useShapeForLightOcclusion(blockState);
    }

    @Override
    public final boolean useShapeForLightOcclusion(BlockState blockState) {
        return abi$useShapeForLightOcclusion(blockState);
    }

    protected boolean abi$isSignalSource(BlockState blockState) {
        return super.isSignalSource(blockState);
    }

    @Override
    public final boolean isSignalSource(BlockState blockState) {
        return abi$isSignalSource(blockState);
    }

    //FluidState abi$getFluidState(BlockState blockState);

    protected boolean abi$hasAnalogOutputSignal(BlockState blockState) {
        return super.hasAnalogOutputSignal(blockState);
    }

    @Override
    public final boolean hasAnalogOutputSignal(BlockState blockState) {
        return abi$hasAnalogOutputSignal(blockState);
    }

    //float abi$getMaxHorizontalOffset();

    //float abi$getMaxVerticalOffset();

    //FeatureFlagSet abi$requiredFeatures();

    protected BlockState abi$rotate(BlockState blockState, Rotation rotation) {
        return super.rotate(blockState, rotation);
    }

    @Override
    public final BlockState rotate(BlockState blockState, Rotation rotation) {
        return abi$rotate(blockState, rotation);
    }

    protected BlockState abi$mirror(BlockState blockState, Mirror mirror) {
        return super.mirror(blockState, mirror);
    }

    @Override
    public final BlockState mirror(BlockState blockState, Mirror mirror) {
        return abi$mirror(blockState, mirror);
    }

    protected boolean abi$canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext);
    }

    @Override
    public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return abi$canBeReplaced(blockState, blockPlaceContext);
    }

    protected boolean abi$canBeReplaced(BlockState blockState, Fluid fluid) {
        return super.canBeReplaced(blockState, fluid);
    }

    @Override
    public final boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return abi$canBeReplaced(blockState, fluid);
    }

    protected List<ItemStack> abi$getDrops(BlockState blockState, BlockPos blockPos, BlockGetter blockGetter, ILootBuilder builder) {
        return super.getDrops(blockState, blockGetter, blockPos, builder);
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, ILootBuilder builder) {
        return abi$getDrops(blockState, blockPos, blockGetter, builder);
    }

    //long abi$getSeed(BlockState blockState, BlockPos blockPos);

    protected VoxelShape abi$getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getOcclusionShape(blockState, blockGetter, blockPos, null);
    }

    @Override
    public final VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return abi$getOcclusionShape(blockState, blockGetter, blockPos);
    }

    protected VoxelShape abi$getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getBlockSupportShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getBlockSupportShape(blockState, blockGetter, blockPos);
    }

    protected VoxelShape abi$getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getInteractionShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getInteractionShape(blockState, blockGetter, blockPos);
    }

    protected int abi$getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getLightBlock(blockState, blockGetter, blockPos, null);
    }

    @Override
    public final int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return abi$getLightBlock(blockState, blockGetter, blockPos);
    }

    //MenuProvider abi$getMenuProvider(BlockState blockState, Level level, BlockPos blockPos);

    protected boolean abi$canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return super.canSurvive(blockState, levelReader, blockPos);
    }

    @Override
    public final boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return abi$canSurvive(blockState, levelReader, blockPos);
    }

    protected float abi$getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getShadeBrightness(blockState, blockGetter, blockPos);
    }

    @Override
    public final float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getShadeBrightness(blockState, blockGetter, blockPos);
    }

    protected int abi$getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection direction) {
        return super.getAnalogOutputSignal(blockState, level, blockPos, direction, null);
    }

    @Override
    public final int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection direction, Object context) {
        return abi$getAnalogOutputSignal(blockState, level, blockPos, direction);
    }

    protected VoxelShape abi$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected VoxelShape abi$getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected VoxelShape abi$getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getVisualShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getVisualShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected void abi$randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.randomTick(blockState, serverLevel, blockPos, source);
    }

    @Override
    public final void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        abi$randomTick(blockState, serverLevel, blockPos, source);
    }

    protected void abi$tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.tick(blockState, serverLevel, blockPos, source);
    }

    @Override
    public final void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        abi$tick(blockState, serverLevel, blockPos, source);
    }

    protected float abi$getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getDestroyProgress(blockState, player, blockGetter, blockPos);
    }

    @Override
    public final float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getDestroyProgress(blockState, player, blockGetter, blockPos);
    }

    protected void abi$spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl, null);
    }

    @Override
    public final void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, Object context) {
        abi$spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
    }

    protected void abi$attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        super.attack(blockState, level, blockPos, player);
    }


    @Override
    public final void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        abi$attack(blockState, level, blockPos, player);
    }

    protected int abi$getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction) {
        return super.getSignal(blockState, blockGetter, blockPos, direction, null);
    }

    @Override
    public final int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
        return abi$getSignal(blockState, blockGetter, blockPos, direction);
    }

    protected void abi$entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity, null);
    }

    @Override
    public final void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, Object context) {
        abi$entityInside(blockState, level, blockPos, entity);
    }

    protected int abi$getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction) {
        return super.getDirectSignal(blockState, blockGetter, blockPos, direction, null);
    }

    @Override
    public final int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
        return abi$getDirectSignal(blockState, blockGetter, blockPos, direction);
    }

    protected void abi$onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        super.onProjectileHit(level, blockState, blockHitResult, projectile);
    }

    @Override
    public final void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        abi$onProjectileHit(level, blockState, blockHitResult, projectile);
    }

    protected boolean abi$propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.propagatesSkylightDown(blockState, blockGetter, blockPos, null);
    }

    @Override
    public final boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return abi$propagatesSkylightDown(blockState, blockGetter, blockPos);
    }

    protected boolean abi$isRandomlyTicking(BlockState blockState) {
        return super.isRandomlyTicking(blockState);
    }

    @Override
    public final boolean isRandomlyTicking(BlockState blockState) {
        return abi$isRandomlyTicking(blockState);
    }

    protected SoundType abi$getSoundType(BlockState blockState) {
        return super.getSoundType(blockState);
    }

    @Override
    public final SoundType getSoundType(BlockState blockState) {
        return abi$getSoundType(blockState);
    }

    // ..

    protected boolean abi$dropFromExplosion(Explosion explosion) {
        return super.dropFromExplosion(explosion);
    }

    @Override
    public final boolean dropFromExplosion(Explosion explosion) {
        return abi$dropFromExplosion(explosion);
    }

    protected BlockState abi$playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return super.playerWillDestroy(blockState, level, blockPos, player, null);
    }

    @Override
    public final BlockState playerWillDestroy(BlockState blockState, Level level, BlockPos blockPos, Player player, Object context) {
        return abi$playerWillDestroy(level, blockPos, blockState, player);
    }

    protected void abi$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        abi$createBlockStateDefinition(builder);
    }

    protected BlockState abi$getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context) {
        return abi$getStateForPlacement(context);
    }

    protected void abi$setPlacedBy(BlockState blockState, Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        abi$setPlacedBy(blockState, level, blockPos, livingEntity, itemStack);
    }

    protected ItemStack abi$getCloneItemStack(BlockState blockState, LevelReader blockGetter, BlockPos blockPos, boolean includeData) {
        return super.getCloneItemStack(blockGetter, blockPos, blockState, includeData, null);
    }

    @Override
    public final ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState, boolean includeData, Object context) {
        return abi$getCloneItemStack(blockState, blockGetter, blockPos, includeData);
    }

    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        abi$appendHoverText(itemStack, tooltips, context);
    }

    protected int abi$getModelTintColor(BlockState blockState, @Nullable BlockGetter level, @Nullable BlockPos blockPos, int layerIndex) {
        return 0xffffffff;
    }

    @Export("called in the TintSource")
    public final int getModelTintColor(BlockState blockState, @Nullable BlockGetter level, @Nullable BlockPos blockPos, int layerIndex) {
        return abi$getModelTintColor(blockState, level, blockPos, layerIndex);
    }
}
