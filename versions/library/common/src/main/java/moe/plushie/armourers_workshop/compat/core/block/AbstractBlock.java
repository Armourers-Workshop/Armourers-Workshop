package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Export;
import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
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

@Available("[1.16, )")
@SuppressWarnings({"deprecation", "NullableProblems"})
public class AbstractBlock extends AbstractBlockImpl {

    public AbstractBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void abi$updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int i, int j) {
        super.updateIndirectNeighbourShapes(blockState, levelAccessor, blockPos, i, j);
    }

    //boolean abi$isPathfindable(BlockState blockState, PathComputationType pathComputationType);

    protected BlockState abi$updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    protected boolean abi$skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return super.skipRendering(blockState, blockState2, direction);
    }

    protected void abi$neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    protected void abi$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);
    }

    protected void abi$onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    protected OpenInteractionResult abi$onAttack(Level level, BlockPos blockPos, BlockState blockState, Direction direction, Player player, OpenInteractionHand hand) {
        return OpenInteractionResult.PASS;
    }

    protected void abi$onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        super.onExplosionHit(blockState, level, blockPos, explosion, biConsumer);
    }

    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return super.useWithoutItem(blockState, level, blockPos, player, interactionHand, blockHitResult, null);
    }

    protected OpenInteractionResult abi$useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult, null);
    }

    protected boolean abi$triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j) {
        return super.triggerEvent(blockState, level, blockPos, i, j);
    }

    protected RenderShape abi$getRenderShape(BlockState blockState) {
        return super.getRenderShape(blockState);
    }

    protected boolean abi$isBed(BlockGetter level, BlockPos blockPos, BlockState blockState, @Nullable Entity entity) {
        return false;
    }

    protected boolean abi$isLadder(BlockGetter level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        return false; // state.is(BlockTags.CLIMBABLE);
    }

    protected boolean abi$useShapeForLightOcclusion(BlockState blockState) {
        return super.useShapeForLightOcclusion(blockState);
    }

    protected boolean abi$isSignalSource(BlockState blockState) {
        return super.isSignalSource(blockState);
    }

    //FluidState abi$getFluidState(BlockState blockState);

    protected boolean abi$hasAnalogOutputSignal(BlockState blockState) {
        return super.hasAnalogOutputSignal(blockState);
    }

    //float abi$getMaxHorizontalOffset();

    //float abi$getMaxVerticalOffset();

    //FeatureFlagSet abi$requiredFeatures();

    protected BlockState abi$rotate(BlockState blockState, Rotation rotation) {
        return super.rotate(blockState, rotation);
    }

    protected BlockState abi$mirror(BlockState blockState, Mirror mirror) {
        return super.mirror(blockState, mirror);
    }

    protected boolean abi$canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext);
    }

    protected boolean abi$canBeReplaced(BlockState blockState, Fluid fluid) {
        return super.canBeReplaced(blockState, fluid);
    }

    protected List<ItemStack> abi$getDrops(BlockState blockState, BlockPos blockPos, BlockGetter blockGetter, ILootBuilder builder) {
        return super.getDrops(blockState, blockGetter, blockPos, builder);
    }

    //long abi$getSeed(BlockState blockState, BlockPos blockPos);

    protected VoxelShape abi$getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getOcclusionShape(blockState, blockGetter, blockPos);
    }

    protected VoxelShape abi$getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getBlockSupportShape(blockState, blockGetter, blockPos);
    }

    protected VoxelShape abi$getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getInteractionShape(blockState, blockGetter, blockPos);
    }

    protected int abi$getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getLightBlock(blockState, blockGetter, blockPos);
    }

    //MenuProvider abi$getMenuProvider(BlockState blockState, Level level, BlockPos blockPos);

    protected boolean abi$canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return super.canSurvive(blockState, levelReader, blockPos);
    }

    protected float abi$getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getShadeBrightness(blockState, blockGetter, blockPos);
    }

    protected int abi$getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
        return super.getAnalogOutputSignal(blockState, level, blockPos, direction);
    }

    protected VoxelShape abi$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected VoxelShape abi$getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected VoxelShape abi$getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getVisualShape(blockState, blockGetter, blockPos, collisionContext);
    }

    protected void abi$randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.randomTick(blockState, serverLevel, blockPos, source);
    }

    protected void abi$tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.tick(blockState, serverLevel, blockPos, source);
    }

    protected float abi$getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getDestroyProgress(blockState, player, blockGetter, blockPos);
    }

    protected void abi$spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
    }

    protected void abi$attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        super.attack(blockState, level, blockPos, player);
    }

    protected int abi$getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return super.getSignal(blockState, blockGetter, blockPos, direction);
    }

    protected void abi$entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity);
    }

    protected int abi$getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return super.getDirectSignal(blockState, blockGetter, blockPos, direction);
    }

    protected void abi$onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        super.onProjectileHit(level, blockState, blockHitResult, projectile);
    }

    protected boolean abi$propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.propagatesSkylightDown(blockState, blockGetter, blockPos);
    }

    protected boolean abi$isRandomlyTicking(BlockState blockState) {
        return super.isRandomlyTicking(blockState);
    }

    protected SoundType abi$getSoundType(BlockState blockState) {
        return super.getSoundType(blockState);
    }

    // ..

    protected boolean abi$dropFromExplosion(Explosion explosion) {
        return super.dropFromExplosion(explosion);
    }

    protected BlockState abi$playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return super.playerWillDestroy(blockState, level, blockPos, player, null);
    }

    protected void abi$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    protected BlockState abi$getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    protected void abi$setPlacedBy(BlockState blockState, Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
    }

    protected ItemStack abi$getCloneItemStack(BlockState blockState, LevelReader blockGetter, BlockPos blockPos) {
        return super.getCloneItemStack(blockGetter, blockPos, blockState);
    }

    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
    }

    protected int abi$getModelTintColor(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos, int layerIndex) {
        return 0xffffffff;
    }

    /// API Implements

    @Override
    public final void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int i, int j) {
        abi$updateIndirectNeighbourShapes(blockState, levelAccessor, blockPos, i, j);
    }

    //boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType);

    @Override
    public final BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return abi$updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public final boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return abi$skipRendering(blockState, blockState2, direction);
    }

    @Override
    public final void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        abi$neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    @Override
    public final void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        abi$onPlace(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public final void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        abi$onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public final void onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        abi$onExplosionHit(blockState, level, blockPos, explosion, biConsumer);
    }

    @Override
    public final OpenInteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        return abi$useWithoutItem(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public final OpenInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        return abi$useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public final boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j) {
        return abi$triggerEvent(blockState, level, blockPos, i, j);
    }

    @Override
    public final RenderShape getRenderShape(BlockState blockState) {
        return abi$getRenderShape(blockState);
    }

    @Override
    public final boolean useShapeForLightOcclusion(BlockState blockState) {
        return abi$useShapeForLightOcclusion(blockState);
    }

    @Override
    public final boolean isSignalSource(BlockState blockState) {
        return abi$isSignalSource(blockState);
    }

    //FluidState getFluidState(BlockState blockState);

    @Override
    public final boolean hasAnalogOutputSignal(BlockState blockState) {
        return abi$hasAnalogOutputSignal(blockState);
    }

    //float getMaxHorizontalOffset();

    //float getMaxVerticalOffset();

    //FeatureFlagSet requiredFeatures();

    @Override
    public final BlockState rotate(BlockState blockState, Rotation rotation) {
        return abi$rotate(blockState, rotation);
    }

    @Override
    public final BlockState mirror(BlockState blockState, Mirror mirror) {
        return abi$mirror(blockState, mirror);
    }

    @Override
    public final boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return abi$canBeReplaced(blockState, blockPlaceContext);
    }

    @Override
    public final boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return abi$canBeReplaced(blockState, fluid);
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, ILootBuilder builder) {
        return abi$getDrops(blockState, blockPos, blockGetter, builder);
    }

    @Override
    public final ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
        return abi$getCloneItemStack(blockState, blockGetter, blockPos);
    }

    //long getSeed(BlockState blockState, BlockPos blockPos);

    @Override
    public final VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getOcclusionShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getBlockSupportShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getInteractionShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getLightBlock(blockState, blockGetter, blockPos);
    }

    //MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos);

    @Override
    public final boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return abi$canSurvive(blockState, levelReader, blockPos);
    }

    @Override
    public final float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getShadeBrightness(blockState, blockGetter, blockPos);
    }

    @Override
    public final int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
        return abi$getAnalogOutputSignal(blockState, level, blockPos, direction);
    }

    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return abi$getVisualShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public final void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        abi$randomTick(blockState, serverLevel, blockPos, source);
    }

    @Override
    public final void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        abi$tick(blockState, serverLevel, blockPos, source);
    }

    @Override
    public final float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$getDestroyProgress(blockState, player, blockGetter, blockPos);
    }

    @Override
    public final void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        abi$attack(blockState, level, blockPos, player);
    }

    @Override
    public final void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl) {
        abi$spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
    }

    @Override
    public final int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return abi$getSignal(blockState, blockGetter, blockPos, direction);
    }

    @Override
    public final void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        abi$entityInside(blockState, level, blockPos, entity);
    }

    @Override
    public final int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return abi$getDirectSignal(blockState, blockGetter, blockPos, direction);
    }

    @Override
    public final void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        abi$onProjectileHit(level, blockState, blockHitResult, projectile);
    }

    @Override
    public final boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return abi$propagatesSkylightDown(blockState, blockGetter, blockPos);
    }

    @Override
    public final boolean isRandomlyTicking(BlockState blockState) {
        return abi$isRandomlyTicking(blockState);
    }

    @Override
    public final SoundType getSoundType(BlockState blockState) {
        return abi$getSoundType(blockState);
    }

    // ..

    @Override
    public final boolean dropFromExplosion(Explosion explosion) {
        return abi$dropFromExplosion(explosion);
    }

    @Override
    public final BlockState playerWillDestroy(BlockState blockState, Level level, BlockPos blockPos, Player player, Object context) {
        return abi$playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        abi$createBlockStateDefinition(builder);
    }

    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context) {
        return abi$getStateForPlacement(context);
    }

    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        abi$setPlacedBy(blockState, level, blockPos, livingEntity, itemStack);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        abi$appendHoverText(itemStack, tooltips, context);
    }

    ///  Runtime API

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param level      The current world
     * @param blockPos   Block position in world
     * @param blockState The current state
     * @param entity     The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    @Export("called in the mixin")
    public final boolean isBed(BlockState blockState, BlockGetter level, BlockPos blockPos, LivingEntity entity) {
        return abi$isBed(level, blockPos, blockState, entity);
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param level      The current world
     * @param blockPos   Block position in world
     * @param blockState The current state
     * @param entity     The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    @Export("called in the mixin")
    public final boolean isLadder(BlockState blockState, LevelReader level, BlockPos blockPos, LivingEntity entity) {
        return abi$isLadder(level, blockPos, blockState, entity);
    }

    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     * <p>
     * Return true if the block is actually destroyed.
     * <p>
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param level      The current world
     * @param blockPos   Block position in world.
     * @param blockState The current state.
     * @param direction  The attack direction.
     * @param player     The player damaging the block.
     * @param hand       The player attack by hand.
     * @return True if the block is actually destroyed.
     */
    @Export("called in the mixin")
    public final OpenInteractionResult attackBlock(Level level, BlockPos blockPos, BlockState blockState, Direction direction, Player player, OpenInteractionHand hand) {
        return abi$onAttack(level, blockPos, blockState, direction, player, hand);
    }

    public final int getModelTintColor(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos, int layerIndex) {
        return abi$getModelTintColor(blockState, level, blockPos, layerIndex);
    }
}
